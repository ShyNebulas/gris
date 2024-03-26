
# Gris

Gris is a statically typed programming language based on Robert Nystrom's Lox from his textbook Crafting Interpreters. 




## Usage/Examples

Clone the project and open in IntelliJ, it will work in other editors but as this was my editor of choice modfications may be required. No extra build data is required, maven will do that for you.

```
https://github.com/ShyNebulas/gris.git
```

Included in ``./samples``
```gris
class Bar {
    message() -> String {
        println("Hello, World!");
    }
}

class Foo < Bar {
    isPalindrome(str: String) -> Boolean {
        if(str == "") {
            return false;
        }
        else {
            val left: Number = 0;
            val right: Number = len(str) - 1;
            while(left < right) {
                if(charAt(str, left) != charAt(str, right)) {
                    return false;
                } else {
                    left = left + 1;
                    right = right - 1;
                }
            }
           return true;
        }
    }
}

val str: String = "civic";
val foobar: Foo = Foo();
println(foobar.isPalindrome(str));
foobar.message();


```

