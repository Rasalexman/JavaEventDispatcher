package src;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 25.03.2017.
 */

public class EventDispatcher {
    // THREAD SAFE SINGLETON IMPLEMENTATION
    private static volatile EventDispatcher ourInstance;
    private Map<String, Map<Object, List<String>>> listHashMap = new HashMap<>();
    public static EventDispatcher getInstance(){
        if (ourInstance == null){
            synchronized (EventDispatcher.class) {
                if (ourInstance == null) {
                    ourInstance = new EventDispatcher();
                }
            }
        }
        return ourInstance;
    }
    private EventDispatcher() {}

    /**
     * Simple Add Event Listener Function
     * @param event - name of event (may be string const)
     * @param o - object who listening an event
     * @param funName - function name (String) ONLY PUBLIC METHODS AVAILABLE for callback event with strong parameters type
     *                public void myCallBack(String event, Object data){
     *                    switch(event){
     *                        case "myCustomEvent": println("do some work with data "+data.toString()); break;
     *                        case "anotherCustomEvent": println("do some work with custom data "+data.toString()); break;
     *                    }
     *                }
     *                where:
     *                - (STRING)"event" - is a name of event, cause u may listen another event in one function
     *                - (OBJECT) "data" - your custom data object (any type)
     *
     * If you are use JAVA 8 use getInstance().listHashMap.putIfAbsent(event, new HashMap<>()); // it's cool feature, but not in android :(((
     *
     * @return
     */
    public static boolean addEventListener(String event, Object o, String funName){
        if(!getInstance().listHashMap.containsKey(event)){
            getInstance().listHashMap.put(event, new HashMap<>());
        }
        Map<Object, List<String>> eventsListeners = getInstance().listHashMap.get(event); // getInstance().listHashMap.putIfAbsent(event, new HashMap<>()); //"java8"
        if(!eventsListeners.containsKey(o)){
            eventsListeners.put(o, new ArrayList<>());
        }
        List<String> allCalbacks = eventsListeners.get(o);  // eventsListeners.putIfAbsent(o, new ArrayList<>());  // "java8"
        boolean isAddedAlready = getInstance().hasCallback(funName, allCalbacks);
        if(!isAddedAlready){
            allCalbacks.add(funName);
        }
        return isAddedAlready;
    }

    /**
     * REMOVE LISTENER FROM OBJECT. Please don't forget to remove ur listener when u dont ever need them, cause GB is work only for null ref
     * @param event - name of event that u are listen
     * @param o - listener object
     * @param funName - name of a listening function
     * @return
     */
    public static boolean removeEventListener(String event, Object o, String funName){
        boolean isRemoved = false;
        if(getInstance().listHashMap.containsKey(event)){
            Map<Object, List<String>> eventsListeners = getInstance().listHashMap.get(event);
            if(eventsListeners.containsKey(o)){
                List<String> allCalbacks = eventsListeners.get(o);
                for(String callback:allCalbacks) {
                    if(callback.equals(funName)){
                        allCalbacks.remove(callback);
                        isRemoved = true;
                        break;
                    }
                }
                // if we dont have any callbacks clear HashMap of EventListeners
                if(allCalbacks.size() == 0){
                    eventsListeners.remove(o);
                }
                // if we dont have any event for listening clear HasMap of Events
                if(eventsListeners.size() == 0){
                    getInstance().listHashMap.remove(event);
                }
            }
        }
        return isRemoved;
    }

    /**
     * CLEAR ALL REFERENCES FROM LISTENING EVENT
     * @param event
     * @return
     */
    public static boolean removeAllListenersFromEvent(String event){
        boolean isRemovedAll = false;
        if(getInstance().listHashMap.containsKey(event)) {
            Map<Object, List<String>> eventsListeners = getInstance().listHashMap.get(event);
            for (Map.Entry<Object, List<String>> entry:eventsListeners.entrySet()) {
                entry.getValue().clear();
            }
            eventsListeners.clear();                    // clear HM
            getInstance().listHashMap.remove(event);    // remove event from HM
            isRemovedAll = true;
        }
        return isRemovedAll;
    }

    /**
     * Dispatch custom event ro listening objects
     * @param event - name of event
     * @param data - any data that u want ot dispatch
     */
    public static void dispatchEvent(String event, Object data){
        if(getInstance().listHashMap.containsKey(event)){
            Map<Object, List<String>> eventsListeners = getInstance().listHashMap.get(event);
            List<String> allCalbacks;
            Object keyObject;
            for (Map.Entry<Object, List<String>> entry : eventsListeners.entrySet()) {
                allCalbacks = entry.getValue();
                keyObject = entry.getKey();
                if(allCalbacks.size() > 0){
                    for (String callback : allCalbacks){
                        try {
                            keyObject.getClass().getDeclaredMethod(callback, String.class, Object.class).invoke(keyObject, event, data);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private boolean hasCallback(String inputEvent, List<String> callbacks){
        boolean isHasCallback = false;
        for (String callback : callbacks){
            if(callback.equals(inputEvent)){
                isHasCallback = true;
                break;
            }
        }
        return isHasCallback;
    }
}
