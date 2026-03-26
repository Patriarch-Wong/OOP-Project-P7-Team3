# OOP Project — Lab P7 Team 3

A 2D platformer/rescue game built with Java and [libGDX](https://libgdx.com/).

The player navigates through hazards (fire), collects items (buckets, wet towels, masks), rescues NPCs, and reaches the exit. Features a scoring system with time bonuses, NPC rescue points, and objective completion.

## Prerequisites

- **Java 8** or newer (JDK, not just JRE)
- No Gradle installation needed — the included Gradle wrapper handles it

### IDE Setup

**IntelliJ IDEA** (recommended):
1. Open the project folder — IntelliJ will detect the Gradle build automatically
2. Wait for the Gradle sync to finish
3. Run `lwjgl3/src/main/java/.../Lwjgl3Launcher.java` or use the Gradle task `lwjgl3:run`

**Eclipse:**
1. Import as a Gradle project via *File → Import → Gradle → Existing Gradle Project*
2. Run `./gradlew eclipse` to generate Eclipse project files if needed
3. Run `Lwjgl3Launcher.java` as a Java application

**VS Code:**
1. Install the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) and [Gradle for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-gradle) extensions
2. Open the project folder — the extensions will detect the Gradle build
3. Run via the Gradle task `lwjgl3:run` in the Gradle sidebar, or run `Lwjgl3Launcher.java` directly

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
