spring-rest-invoker
===================

Spring invoker which maps remote REST services to local interfaces. 


## Using

Make an interface, i.e:

```java
public interface BookService {

    @RequestMapping("/volumes")
    QueryResult findBooksByTitle(@RequestParam("q") String q);
    
    @RequestMapping("/volumes/{id}")
    Item findBookById(@PathVariable("id") String id);
}
```

Note that the annotations are from spring's web package.

and then map it to the remote REST URL you want to consume:

```xml
<bean id="RemoteBookService"
		class="com.github.ggeorgovassilis.springjsonmapper.HttpJsonInvokerFactoryProxyBean">
		<property name="baseUrl" value="https://www.googleapis.com/books/v1" />
		<property name="remoteServiceInterfaceClass" value="com.github.ggeorgovassilis.springjsonmapper.BookService"/>
</bean>
```

## TODO

- Implement POST,PUT,DELETE etc
- Post objects other than primitives
