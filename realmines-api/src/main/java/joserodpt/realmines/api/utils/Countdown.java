package joserodpt.realmines.api.utils;

/*
 * A simple countdown timer using the Runnable interface in seconds! <b>Great
 * for minigames and other shiz?</b>
 * <p>
 * Project created by
 *
 * @author ExpDev
 */

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class Countdown implements Runnable {

    // Main class for bukkit scheduling
    private final JavaPlugin plugin;

    // Our scheduled task's assigned id, needed for canceling
    private Integer assignedTaskId;

    // Seconds and shiz
    private final int seconds;
    private int secondsLeft;

    // Actions to perform while counting down, before and after
    private final Consumer<Countdown> everySecond;
    private final Runnable beforeTimer;
    private final Runnable afterTimer;

    // Construct a timer, you could create multiple so for example if
    // you do not want these "actions"
    public Countdown(final JavaPlugin plugin, final int seconds, final Runnable beforeTimer, final Runnable afterTimer,
                     final Consumer<Countdown> everySecond) {
        // Initializing fields
        this.plugin = plugin;

        this.seconds = seconds;
        this.secondsLeft = seconds;

        this.beforeTimer = beforeTimer;
        this.afterTimer = afterTimer;
        this.everySecond = everySecond;
    }

    public static String format(final long mills) {
        long seconds = mills / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        StringBuilder formattedTime = new StringBuilder();
        if (days > 0) {
            formattedTime.append(days).append("d ");
        }
        if (hours > 0) {
            formattedTime.append(hours).append("h ");
        }
        if (minutes > 0) {
            formattedTime.append(minutes).append("m ");
        }
        if (seconds > 0 || (mills > 0 && formattedTime.length() == 0)) {
            formattedTime.append(seconds).append("s");
        }

        return formattedTime.toString().trim();
    }

    /**
     * Runs the timer once, decrements seconds etc... Really wish we could make it
     * protected/private so you couldn't access it
     */
    @Override
    public void run() {
        // Is the timer up?
        if (this.secondsLeft < 1) {
            // Do what was supposed to happen after the timer
            this.afterTimer.run();

            // Cancel timer
            if (this.assignedTaskId != null)
                Bukkit.getScheduler().cancelTask(this.assignedTaskId);
            return;
        }

        // Are we just starting?
        if (this.secondsLeft == this.seconds)
            this.beforeTimer.run();

        // Do what's supposed to happen every second
        this.everySecond.accept(this);

        // Decrement the seconds left
        --this.secondsLeft;
    }

    /**
     * Gets the total seconds this timer was set to run for
     *
     * @return Total seconds timer should run
     */
    public int getTotalSeconds() {
        return this.seconds;
    }

    /**
     * Gets the seconds left this timer should run
     *
     * @return Seconds left timer should run
     */
    public int getSecondsLeft() {
        return this.secondsLeft;
    }

    public Integer getTaskId() {
        return this.assignedTaskId;
    }

    public void killTask() {
        Bukkit.getScheduler().cancelTask(this.assignedTaskId);
    }

    /**
     * Schedules this instance to "run" every second
     */
    public void scheduleTimer() {
        // Initialize our assigned task's id, for later use so we can cancel
        this.assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, this, 0L, 20L);
    }
}