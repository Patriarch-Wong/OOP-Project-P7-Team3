# OOP Project — Lab P7 Team 3

A 2D platformer/rescue game built with Java and [libGDX](https://libgdx.com/).

The player navigates through hazards (fire), collects items (buckets, wet towels, masks), rescues NPCs, and reaches the exit. Features a scoring system with time bonuses, NPC rescue points, and objective completion.

## Prerequisites

- **Java 8** or newer (JDK, not just JRE)
- No Gradle installation needed — the included Gradle wrapper handles it

## How to Run

**Run directly:**

```bash
./gradlew lwjgl3:run
```

On Windows, use `gradlew.bat lwjgl3:run` instead.

**Build a JAR:**

```bash
./gradlew lwjgl3:jar
```

The runnable JAR will be at `lwjgl3/build/libs/P7-Team3-1.0.0.jar`. Run it with:

```bash
java -jar lwjgl3/build/libs/P7-Team3-1.0.0.jar
```

## Running Tests

```bash
./gradlew test
```

## Project Structure

```
core/           Main game logic
  engine/       Reusable engine layer (entities, collision, movement, scenes, audio, I/O)
  game/         Game-specific code (entities, scenes, UI, physics, scoring, status effects)
lwjgl3/         Desktop launcher (LWJGL3 backend)
assets/         Sprites, audio, backgrounds, UI images
```
