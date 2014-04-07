spring-rest-invoker
===================

Spring proxy that imports remote JSON REST services as local java interfaces. It uses spring web annotations such as @RequestParam to know which remote URL to contact (and how) when you call a method on the proxied service interface.

## News

2014-04-04: version 0.0.3-SNAPSHOT is out with support for more HTTP methods such as PUT, DELETE etc.


## Using

### 0. Getting

Either build it yourself or add the repository to your pom:
```xml
<repository>
  <id>spring-rest-invoker</id>
  <url>http://ggeorgovassilis.github.com/spring-rest-invoker/repository/</url>
</repository>
```

Then include the dependency:

```xml
<dependency>
	<groupId>com.github.ggeorgovassilis</groupId>
	<artifactId>spring-rest-invoker</artifactId>
	<version>0.0.3-SNAPSHOT</version>
</dependency>
```

### 1. Make an interface, i.e:

```java
public interface BookService {

    @RequestMapping("/volumes")
    QueryResult findBooksByTitle(@RequestParam("q") String q);
    
    @RequestMapping("/volumes/{id}")
    Item findBookById(@PathVariable("id") String id);
}
```

Note that the annotations are from spring's web package.

### 2. Then map it to the remote REST URL you want to consume:

```xml
<bean id="RemoteBookService"
		class="com.github.ggeorgovassilis.springjsonmapper.HttpJsonInvokerFactoryProxyBean">
		<property name="baseUrl" value="https://www.googleapis.com/books/v1" />
		<property name="remoteServiceInterfaceClass" value="com.github.ggeorgovassilis.springjsonmapper.BookService"/>
</bean>
```

### 3. Use it in your code

```java
...
@Autowired
RemoteBookService bookService;

...
QueryResult results = bookService.findBooksByTitle("Alice in Wonderland");

```

### 4. Examples

You can POST an object:

```java
public interface AnimasciService {

    @RequestMapping(value="/", method=RequestMethod.POST)
    Animation createNewAnimation(@RequestBody Animation animation);

}
```

Or post multiple objects (in which case you also need to provide names for them with a @RequestParam):

```java
public interface BankService {
    @RequestMapping(value="/transfer", method=RequestMethod.POST)
    Account transfer(
	    @RequestBody @RequestParam("fromAccount") Account fromAccount, 
	    @RequestBody @RequestParam("actor") Customer actor,
	    @RequestBody @RequestParam("toAccount") Account toAccount,
	    @RequestBody @RequestParam("amount") int amount,
	    @RequestParam("sendConfirmationSms") boolean sendConfirmationSms);

    @RequestMapping(value="/verify", method=RequestMethod.POST)
    Boolean checkAccount(@RequestBody Account account);
}
```

which will post a JSON object similar to this:
```javascript
{
	"fromAccount":{....},
	"actor":{...},
	"toAccount":{....},
	"amount":123
}
```

## F.A.Q.

#### What does @RequestParam do?

Method arguments annotated with @RequestParam are, unless otherwise specified, taken as strings and passed as HTTP parameters, e.g.

```java
public interface BookService {

    @RequestMapping(value="/books")
    Book findBook(@RequestParam("isbn") String isbn);

}

...

bookService.findBook("123");
```

an invocation of ```findBook``` will result in an HTTP GET request to this url: ```/books?isbn=123```

When there is also a @RequestBody, then the handling is different - look further down the F.A.Q.

#### What does @RequestBody do?

You might want to send JSON to a REST service via an HTTP POST request. Method arguments annotated with @ReqeustBody are serialized into JSON and sent to the remote service. If there is only a single method argument annotated with @RequestBody, then that argument is serialized and sent over. If multiple arguments are annotated, then each @RequestBody needs to be accompanied by a @RequestParam which specifies the field name of the object. 

In an ideal world we wouldn't need @RequestParam because the invoker would, supposedly, be able to read method argument names and pick URL parameter names accordingly; in Java that's suprisingly hard to do since the reflection API does not expose method argument names.

For example:

```java
public interface BookService {

    @RequestMapping(value="/books", method=RequestMethod.POST)
    void saveBook(@RequestBody Book book);

}

...

bookService.saveBook(book);
```

will result in this JSON being posted to ```/books```:

```javascript
{
	"name":"Some Book Title",
	"author":"John Doe",
	"genres":["technology","educational"],
	"availability":{
			"available":true,
			"itemsInStock":4
			}
}			
```

Using multiple arguments:

```java
public interface BookService {

    @RequestMapping(value="/books", method=RequestMethod.POST)
    void saveBook(@RequestBody @RequestParam("book") Book book, @RequestBody @RequestParam("availability") availability);

}

...

bookService.saveBook(book, availability);
```

will result in this JSON being posted to ```/books```:

```javascript
{
	"book":{
		"name":"Some Book Title",
		"author":"John Doe",
		"genres":["technology","educational"]
	},
	"availability":{
			"available":true,
			"itemsInStock":4
			}
}			
```


#### What does @PathVariable do?

Some REST services incorporate parameters in the URL path rather than URL parameters, i.e.: ```example.com/service/findBooks/isbn/1234``` as opposed to ```example.com/service/findBooks?isbn=1234````

@PathVariable is specified together with a @RequestParam and indicates the the method argument is not to be sent as a URL parameter. Note that you need to specify a matching placeholder with @RequestMapping:

```
public interface BookService {

    @RequestMapping("/volumes/{id}")
    Item findBookById(@PathVariable("id") String id);
}
```

Note the ```{id}``` notation in @RequestMapping; it needs to match the one specificed in @PathVariable
