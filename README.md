How to install: 
1) Download .jar file
2) In intellij idea go to File -> Settings
3) Go to Plugins
4) Click to settings icon near "Marketplace" and "Installed"
5) Click "Install Plugin from disk" and choose .jar 
6) Restart IDE

Checking rules for now:

1) Returning 'null' in method:
```java
public Integer calc(int a, int b) {
    if(a < 0 || b < 0) {
        return null;    
    }
    // calc
} 
```
must be:
```java
public Integer calc(int a, int b) {
    if(a < 0 || b < 0) {
        // or throw exception
        // or new YourEntity extends Number with constructor (int val)
        return 0;
    }
    // calc    
} 
```

2) Method has to have only one 'return' statement!

(There might be better example)
```java
public boolean greatherThan(int a) {
    if(a > 0) {
        return true;
    }
    return false;
}
```
must be:
```java
public boolean greatherThan(int a) {
    final boolean result;
    if(a > 0) {
        result = true;
    } else {
        result = false;    
    }
    return result;
}
```

3) Checking for all final class & method variables for immutability.

Also checking when ```for(int i = 0;)``` ```i``` may be replaced by objects iteration

