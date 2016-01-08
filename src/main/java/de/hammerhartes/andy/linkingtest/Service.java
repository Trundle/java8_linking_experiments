package de.hammerhartes.andy.linkingtest;

public class Service {

    public void run() throws NoSuchMethodException {
        final Router router = new Router();
        final OtherResourceHandler otherResourceHandler = new OtherResourceHandler(router);
        System.out.println(otherResourceHandler.linkToScreens());
        System.out.println(otherResourceHandler.linkToOneScreen());
    }

    public static void main(final String... args) throws NoSuchMethodException {
        new Service().run();
    }
}
