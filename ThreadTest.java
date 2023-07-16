import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.util.Random;

public class ThreadTest extends Application {

    public static final double SIZE = 400;

    @Override
    public void start(Stage primaryStage) {
        Pane pane = new Pane();

        pane.setOnMouseClicked(e -> {
            Mover m = new Mover(e.getX(), e.getY());
            pane.getChildren().add(m);
            Thread t = new Thread(m);
            t.start();
        });

        Scene scene = new Scene(pane, SIZE, SIZE);

        primaryStage.setTitle("Thread Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class Mover extends Group implements Runnable {

    public static final double SIZE = 20;
    public static final double GRAVITY = 0.5;

    private double dx;
    private double dy;
    private long sleepTime;
    private long lifeTime;
    private Circle circle;

    public Mover(double x, double y) {
        Random random = new Random();
        dx = random.nextDouble() * 2 - 1; // will chose random value between -1 and 1 for starting x so it can spawn moving either way
        dy = 0;
        sleepTime = 20;
        lifeTime = 2000 + random.nextInt(8000);

        circle = new Circle(0, 0, Math.random() * SIZE);
        circle.setFill(Color.color(Math.random(), Math.random(), Math.random()));

        this.setTranslateX(x);
        this.setTranslateY(y);

        this.getChildren().addAll(circle);
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < lifeTime) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                moving();
                animateSize();
            });
        }

        Platform.runLater(() -> {
            if (this.getParent() instanceof Pane) {
                Pane parentPane = (Pane) this.getParent();
                parentPane.getChildren().remove(this);
            }
        });
    }

    public void moving() {
        Parent parent = this.getParent();
        if (parent == null || !(parent instanceof Pane))
            return;

        Pane pane = (Pane) parent;

        double curX = this.getTranslateX() + dx;
        double curY = this.getTranslateY();

        dy += GRAVITY;
        if (curY > pane.getHeight() - SIZE) {
            dy *= -0.7; //loses bounce over time
            curY = pane.getHeight() - SIZE;
        }
        if (curX > pane.getWidth() - circle.getRadius() || curX < circle.getRadius()) {// bounce off the sides
            dx *= -0.7;
            curX = curX < circle.getRadius() ? circle.getRadius() : pane.getWidth() - circle.getRadius();
        }

        this.setTranslateX(curX);
        this.setTranslateY(curY + dy);

        this.setRotate(this.getRotate() + 1);
    }

    public void animateSize() {
        double newSize = circle.getRadius() + (Math.random() - 0.5); // will change size randomly it kinda makes the circles look wobbly
        if (newSize > 0 && newSize < SIZE) {
            circle.setRadius(newSize);
        }
    }
}
