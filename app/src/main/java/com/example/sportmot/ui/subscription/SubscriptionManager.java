package com.example.sportmot.ui.subscription;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class SubscriptionManager {
    private static final String PREFS_NAME = "subscriptions";
    private static final String KEY_SUBSCRIBED_TEAMS = "subscribed_teams";
    private SharedPreferences prefs;
    public SubscriptionManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    public void subscribeToTeam(String teamId) {
        Set<String> subscribedTeams = new HashSet<>(prefs.getStringSet(KEY_SUBSCRIBED_TEAMS, new HashSet<>()));
        subscribedTeams.add(teamId);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(KEY_SUBSCRIBED_TEAMS, subscribedTeams);
        editor.apply();
    }
    public Set<String> getSubscribedTeams() {
        return prefs.getStringSet(KEY_SUBSCRIBED_TEAMS, new HashSet<>());
    }
    public boolean isSubscribed(String teamId) {
        return getSubscribedTeams().contains(teamId);
    }
}