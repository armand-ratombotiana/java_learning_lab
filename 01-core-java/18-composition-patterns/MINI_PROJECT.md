# Mini Project: UI Component Framework

## Objective
Build a mini UI component framework that demonstrates both the Composite Pattern (to build a tree of UI elements) and the Decorator Pattern (to add visual borders and scrolling capabilities to those elements).

## Prerequisites
*   Java 17+

## Step 1: The Base Interface (Component)
Define the common interface for all UI elements.

```java
public interface UIWidget {
    void draw();
    int getWidth();
    int getHeight();
}
```

## Step 2: The Leaf Nodes
Create basic, indivisible UI elements.

```java
public class TextLabel implements UIWidget {
    private final String text;

    public TextLabel(String text) {
        this.text = text;
    }

    @Override
    public void draw() {
        System.out.println("Drawing Text: [" + text + "]");
    }

    @Override public int getWidth() { return text.length() * 10; }
    @Override public int getHeight() { return 20; }
}

public class Button implements UIWidget {
    private final String label;

    public Button(String label) {
        this.label = label;
    }

    @Override
    public void draw() {
        System.out.println("Drawing Button: <" + label + ">");
    }

    @Override public int getWidth() { return label.length() * 12; }
    @Override public int getHeight() { return 30; }
}
```

## Step 3: The Composite Node
Create a container that can hold multiple `UIWidget`s. This demonstrates the Composite Pattern.

```java
import java.util.ArrayList;
import java.util.List;

public class Panel implements UIWidget {
    private final List<UIWidget> children = new ArrayList<>();

    public void addWidget(UIWidget widget) {
        children.add(widget);
    }

    @Override
    public void draw() {
        System.out.println("--- Panel Start ---");
        for (UIWidget child : children) {
            child.draw(); // Delegate drawing to children
        }
        System.out.println("--- Panel End ---");
    }

    @Override
    public int getWidth() {
        return children.stream().mapToInt(UIWidget::getWidth).max().orElse(0);
    }

    @Override
    public int getHeight() {
        return children.stream().mapToInt(UIWidget::getHeight).sum();
    }
}
```

## Step 4: The Decorator Base Class
Create an abstract decorator that implements `UIWidget` and holds a reference to a `UIWidget`.

```java
public abstract class WidgetDecorator implements UIWidget {
    protected final UIWidget widget;

    public WidgetDecorator(UIWidget widget) {
        this.widget = widget;
    }

    @Override
    public void draw() {
        widget.draw(); // Default forwarding
    }

    @Override
    public int getWidth() {
        return widget.getWidth();
    }

    @Override
    public int getHeight() {
        return widget.getHeight();
    }
}
```

## Step 5: Concrete Decorators
Create specific decorators that add new behavior or modify existing behavior.

```java
public class BorderDecorator extends WidgetDecorator {
    public BorderDecorator(UIWidget widget) {
        super(widget);
    }

    @Override
    public void draw() {
        System.out.println("====================");
        super.draw(); // Draw the wrapped widget
        System.out.println("====================");
    }
}

public class ScrollbarDecorator extends WidgetDecorator {
    public ScrollbarDecorator(UIWidget widget) {
        super(widget);
    }

    @Override
    public void draw() {
        super.draw();
        System.out.println(" [Scrollbar Attached] ");
    }
}
```

## Step 6: Test the Framework
Assemble the components dynamically at runtime.

```java
public class Main {
    public static void main(String[] args) {
        // 1. Create simple leaf nodes
        UIWidget title = new TextLabel("Welcome to the App");
        UIWidget loginBtn = new Button("Login");
        UIWidget signupBtn = new Button("Sign Up");

        // 2. Create a Composite (Panel) and add leaves
        Panel loginForm = new Panel();
        loginForm.addWidget(title);
        loginForm.addWidget(loginBtn);
        loginForm.addWidget(signupBtn);

        // 3. Decorate the entire panel!
        // We add a border, and then we add a scrollbar to the bordered panel.
        UIWidget finalUI = new ScrollbarDecorator(new BorderDecorator(loginForm));

        // 4. Draw everything
        System.out.println("Total Width: " + finalUI.getWidth());
        System.out.println("Total Height: " + finalUI.getHeight());
        finalUI.draw();
    }
}
```

## Expected Output
```text
Total Width: 180
Total Height: 80
--- Panel Start ---
Drawing Text: [Welcome to the App]
Drawing Button: <Login>
Drawing Button: <Sign Up>
--- Panel End ---
 [Scrollbar Attached] 
```