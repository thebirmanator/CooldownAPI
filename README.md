# CooldownAPI

API for plugins dealing with cooldowns

<h2> How to Use </h2>

Remember to put `CooldownAPI` into the dependencies of your plugin! 

<h3> Constructing a cooldown </h3>

When creating a new cooldown simply create a new object:
```java
new Cooldown(Player player, String code, int duration);
```

`player` is a Bukkit player to apply the cooldown to.

`code` is a specific string used to check for different types of cooldowns. Can be
anything.

`duration` is how long the cooldown should last in seconds.

<h3>Methods</h3>

Get the UUID of the player associated with the cooldown<br>
`getPlayerUUID()` 

Get the cooldown code<br>
`getCode()`

Get the end time of the cooldown (Unix Timestamp)<br>
`getEndTime()`

Set the duration (time in seconds). Keep in mind that it uses the time the cooldown was constructed to recalculate this<br>
`setDuration(int duration)`

Get the time left on a cooldown (Unix Timestamp)<br>
`getTimeRemaining()`

Get the formatted time remaining on a cooldown "00hr00m00s"<br>
`getFormattedTimeLeft()`

Remove the cooldown from the config<br>
`remove()`

Get a Set of all available Cooldowns<br>
`getCooldowns()`

Check if a cooldown has expired. Returns true if the cooldown is expired, otherwise returns false.<br>
`isExpired()`
<br><i>If you are retrieving a cooldown from `Cooldown.getCooldown`, make sure it exists first: `cooldown == null || cooldown.isExpired()`.</i>
<br>

<h3>Examples</h3>

Gets all cooldowns associated with a specific player
```java
Set<Cooldown> cooldowns = Cooldown.getCooldowns(Player player);
```

Get a specific Cooldown for a player, code being the id of the cooldown. Returns null if the cooldown hasn't been set
```java
Cooldown cooldown = Cooldown.getCooldown(Player player, String code);
```
