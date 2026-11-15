package tanhuo;

final class Companion {
    static final String NAME = "tanhoohoo";

    private boolean invited;
    private boolean guardReady = true;

    boolean isInvited() {
        return invited;
    }

    String invite() {
        invited = true;
        return "tanhoohoo: Let's go see what's out there.";
    }

    boolean blockHit() {
        if (!invited || !guardReady) return false;
        guardReady = false;
        return true;
    }

    void rest() {
        guardReady = true;
    }

    String status() {
        if (!invited) return "Waiting at Home Land";
        return guardReady ? "Alongside you, ready to protect" : "Alongside you, protection returns after resting";
    }
}
