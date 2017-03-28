# JavaEventDispatcher
Simple Thread-Safe Java Singleton to dispatch event to any listening object. It's use a java reflection to dispatch event to functions. 

### Usage
```java
public static void main(...args){
  // subscribe for listening events
  EventDispatcher.addEventListener("myCustomEvent", this, "dataReadyEventHandler");
  EventDispatcher.addEventListener("myCustomEvent2", this, "dataReadyEventHandler");
  EventDispatcher.addEventListener("myCustomEvent3", this, "anotherEventHandler");
  // u can use any custom Classes to Listen the same event
  MyCustomClass myCustomEventHandler = new MyCustomClass();
  EventDispatcher.addEventListener("myCustomEvent3", myCustomEventHandler, "myCustomEventHandlerFunction");

  // dispatch events and pass some data in it
  EventDispatcher.dispatchEvent("myCustomEvent", new ArrayList());
  EventDispatcher.dispatchEvent("myCustomEvent2", new HashMap<String, Object>());
  EventDispatcher.dispatchEvent("myCustomEvent3", "HELLO FROM EVENT DISPATCHER");

  // remove event listeners
  EventDispatcher.removeAllListenersFromEvent("myCustomEvent");
  EventDispatcher.removeEventListener("myCustomEvent2", this, "dataReady");
  EventDispatcher.removeAllListenersFromEvent("myCustomEvent3");
}

/**
     * You can listen another events in one function (ONLY PUBLIC)
     * @param event - name of event
     * @param data - your data object
     */
public void dataReadyEventHandler(String event, Object data){
  switch(event){
    case "myCustomEvent": println("DO SMTHNG WITH DATA "+data.toString()); break;
    case "myCustomEvent2": println("DO SMTHNG WITH DATA 2"+data.toString()); break;
  }
}

public void anotherEventHandler(String event, Object data){
  println("ANOTHER EVENT FUNCTION");
}
```
