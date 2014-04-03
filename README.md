spring-rest-invoker
===================

Spring proxy that allows you to use remote JSON REST services as local java interfaces. It uses spring web annotations such as @RequestParam to know which remote URL to contact (and how) when you call a method on the proxied service interface.


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
	<version>0.0.2-SNAPSHOT</version>
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

## TODO

- Implement PUT,DELETE etc
