package com.grader.simulator.model;


public class Rank {

    public final String emoji;
    public final String title;
    public final String description;
    public final int requiredGrades; 
    public final int nextRankGrades;

    public Rank(String emoji, String title, String description,
                int requiredGrades, int nextRankGrades) {
        this.emoji = emoji;
        this.title = title;
        this.description = description;
        this.requiredGrades = requiredGrades;
        this.nextRankGrades = nextRankGrades;
    }


    public static final Rank[] ALL_RANKS = {

        new Rank(
            "🎓",
            "Тун-Тун Отчислюн",
            "Ох, пойдёт щас возня)",
            0, 10
        ),

        new Rank(
            "😤",
            "Строгий препод",
            "Уже начинает входить во вкус",
            10, 25
        ),

        new Rank(
            "👿",
            "Гроза семестра",
            "Студенты пересаживаются подальше",
            25, 50
        ),

        new Rank(
            "☠️",
            "Палач зачётки",
            "Деканат выдал тебе постоянный пропуск",
            50, 100
        ),

        new Rank(
            "🔥",
            "Повелитель пересдач",
            "Студенты видят вас в ночных кошмарах",
            100, 200
        ),

        new Rank(
            "⚡",
            "Карающий Молот",
            "Один взгляд — и уже двойка",
            200, 350
        ),

        new Rank(
            "💀",
            "Жнец среднего балла",
            "Средний балл курса упал ниже плинтуса",
            350, 500
        ),

        new Rank(
            "👺",
            "Демон сессионной тьмы",
            "Тебя боятся сильнее итоговых экзаменов",
            500, 750
        ),

        new Rank(
            "🌑",
            "Апокалипсис сессии",
            "Студенты уходят сами, не дожидаясь двойки",
            750, 1000
        ),

        new Rank(
            "👁️",
            "ОН — Неназываемый",
            "Легенда. Миф. Работает 40 лет без пересдач.",
            1000, 0
        ),
    };

    public static Rank getCurrentRank(int totalGrades) {
        Rank current = ALL_RANKS[0];
        for (Rank rank : ALL_RANKS) {
            if (totalGrades >= rank.requiredGrades) {
                current = rank;
            } else {
                break;
            }
        }
        return current;
    }

    public static Rank checkNewRankUnlocked(int previousGrades, int newGrades) {
        Rank prevRank = getCurrentRank(previousGrades);
        Rank newRank  = getCurrentRank(newGrades);
        if (!prevRank.title.equals(newRank.title)) {
            return newRank;
        }
        return null;
    }

    public static String getProgressText(int totalGrades) {
        Rank current = getCurrentRank(totalGrades);
        if (current.nextRankGrades == 0) {
            return "🏆 Максимальное звание достигнуто!";
        }
        int remaining = current.nextRankGrades - totalGrades;
        return "До следующего звания: " + remaining + " двоек";
    }
}
