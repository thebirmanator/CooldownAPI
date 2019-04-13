# DarksCooldownAPI

API for plugins dealing with cooldowns

<h2> How to Use </h2>

Remember to put 'DarksCooldownAPI' into the dependencies of your plugin! 

<h3> Constructing a cooldown </h3>

New cooldown:
```java
new Cooldown(Player player, String code, int duration);
```
<br>

`player` is a Bukkit player to apply the cooldown to

`code` is a specific string used to check for different types of cooldowns. Can be
anything.

`duration` is how long the cooldown should last in seconds

<h3> Methods </h3>

`getPlayerUUID();`
<br>gets the player's UUID 

`getCode();`
<br>gets the code for this cooldown

`getEndTime();`
<br>gets the time the cooldown is supposed to end, in unix time

`setDuration(int duration);`
<br>sets the duration time in seconds. Keep in mind that it uses the time the 
cooldown was constructed to recalculate this

`getTimeRemaining();`
<br>gets the time left, in Unix time, for this cooldown to expire

`getFormattedTimeLeft();`
<br>gets the time remaining in the format "00hr00m00s"

`remove();`
<br>removes the cooldown from the config, and will no longer be found in 
`getCooldowns()`

`isExpired();`
<br>returns true if the cooldown is expired, otherwise returns false

<br>

gets all cooldowns for a specific player
```java
Cooldown.getCooldowns(Player player);
```

gets a specific Cooldown for a player and code. Returns null if the cooldown hasn't been set
```java
Cooldown.getCooldown(Player player, String code);
```