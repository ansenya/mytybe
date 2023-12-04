package org.example;

import java.util.HashMap;
import java.util.Scanner;

public class Main {

    static Scanner in = new Scanner(System.in);
    static HashMap<String, String> responses = new HashMap<>();

    public static void main(String[] args) {
        Ai ai = new Ai();
        ai.start();
    }

    static class Ai {

        private Ai yes;
        private Ai no;

        boolean fl = false;

        private String animal;
        private String tag;

        public void start() {
            if (yes == null && no == null) {
                System.out.println("Сдаюсь. Кто это?");
                animal = in.next();
                tag = in.next();
            }
            yes = new Ai(animal, tag);
            yes.fl = true;
            while (true) {
                System.out.println();
                System.out.println("Начнем!");
                responses.clear();
                yes.guess();
            }
        }

        private void guess() {
            String key;
            if (responses.containsKey(tag)) {
                key = responses.get(tag);
            } else {
                System.out.printf("У него есть %s?\n", tag);
                key = in.next();
                responses.put(tag, key);
            }
            if (key.equals("да")) {
                if (yes == null) {
                    if (responses.containsKey(animal)) {
                        key = responses.get(animal);
                    } else {
                        System.out.printf("Это %s?\n", animal);
                        key = in.next();
                        responses.put(animal, key);
                    }
                    if (key.equals("нет")) {
                        System.out.println("Сдаюсь. Кто это?");
                        yes = new Ai(in.next(), in.next());
                        yes.no = new Ai(animal, tag);
                    }
                } else {
                    yes.guess();
                }
            } else {
                if (no == null) {
                    System.out.println("Сдаюсь. Кто это?");
                    no = new Ai(in.next(), in.next());
                } else {
                    no.guess();
                }
            }
        }

        public Ai(String animal, String tag) {
            this.animal = animal;
            this.tag = tag;
        }

        public Ai() {
        }
    }
}
