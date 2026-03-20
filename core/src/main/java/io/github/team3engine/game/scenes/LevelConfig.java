package io.github.team3engine.game.scenes;

public class LevelConfig {
    public final int levelNumber;
    public final float worldWidth;
    public final float worldHeight;
    public final float timerDuration;
    public final float playerMaxHp;
    public final float npcMaxHp;
    public final float playerStartX;
    public final float playerStartY;
    public final float exitX;
    public final float npcX;
    public final float npcY;
    public final float[] groundSegmentsX;
    public final float[] groundSegmentsWidth;
    public final float[] platformX;
    public final float[] platformY;
    public final float[] platformWidth;
    public final float[] groundFireX;
    public final float[] ceilingFireX;
    public final float[] ceilingFireY;
    public final float[] towelX;
    public final float[] towelY;
    public final float[] maskX;
    public final float[] maskY;
    public final String nextScene;
    public final String displayName;

    public LevelConfig(Builder builder) {
        this.levelNumber = builder.levelNumber;
        this.worldWidth = builder.worldWidth;
        this.worldHeight = builder.worldHeight;
        this.timerDuration = builder.timerDuration;
        this.playerMaxHp = builder.playerMaxHp;
        this.npcMaxHp = builder.npcMaxHp;
        this.playerStartX = builder.playerStartX;
        this.playerStartY = builder.playerStartY;
        this.exitX = builder.exitX;
        this.npcX = builder.npcX;
        this.npcY = builder.npcY;
        this.groundSegmentsX = builder.groundSegmentsX;
        this.groundSegmentsWidth = builder.groundSegmentsWidth;
        this.platformX = builder.platformX;
        this.platformY = builder.platformY;
        this.platformWidth = builder.platformWidth;
        this.groundFireX = builder.groundFireX;
        this.ceilingFireX = builder.ceilingFireX;
        this.ceilingFireY = builder.ceilingFireY;
        this.towelX = builder.towelX;
        this.towelY = builder.towelY;
        this.maskX = builder.maskX;
        this.maskY = builder.maskY;
        this.nextScene = builder.nextScene;
        this.displayName = builder.displayName;
    }

    public static LevelConfig[] getAllLevels() {
        return new LevelConfig[] {
            createLevel1(),
            createLevel2(),
            createLevel3()
        };
    }

    private static LevelConfig createLevel1() {
        return new Builder(1, 2000f, 800f)
            .timerDuration(90f)
            .playerMaxHp(100f)
            .npcMaxHp(100f)
            .playerStartX(200f)
            .playerStartY(40f)
            .exitX(1950f)
            .npcX(1000f)
            .npcY(40f)
            .groundSegments(40f, new float[]{0, 350, 750, 1100, 1500}, new float[]{250, 400, 350, 400, 500})
            .platforms(new float[]{0, 350, 620, 900, 1150, 1400, 1900},
                       new float[]{130, 210, 300, 180, 260, 150, 220},
                       new float[]{200, 220, 200, 180, 200, 250, 200})
            .groundFires(new float[]{150, 400, 480, 800, 1150, 1250, 1600, 1700})
            .ceilingFires(new float[]{100, 360, 750, 950, 1180, 1430},
                           new float[]{130, 210, 300, 180, 260, 150})
            .towels(new float[]{180, 920, 1420}, new float[]{175, 225, 195})
            .masks(new float[]{550, 1170}, new float[]{75, 305})
            .nextScene(SceneType.SCORE_BOARD.name())
            .displayName("Level 1 - The Escape")
            .build();
    }

    private static LevelConfig createLevel2() {
        return new Builder(2, 2500f, 800f)
                .timerDuration(60f)
                .playerMaxHp(100f)
                .npcMaxHp(80f)
                .playerStartX(200f)
                .playerStartY(40f)
                .exitX(1950f)
                .npcX(1000f)
                .npcY(190f)
                .groundSegments(40f, new float[]{0, 650, 1100, 1500}, new float[]{250, 400, 350, 400, 500})
                .platforms(new float[]{200, 350, 620, 900, 1150, 1400, 1900},
                           new float[]{130, 210, 250, 180, 250, 150, 220},
                           new float[]{200, 220, 200, 180, 200, 250, 200})
                .groundFires(new float[]{100, 1150,1200 ,1250, 1300, 1600, 1700})
                .ceilingFires(new float[]{280, 360, 650, 950, 1180, 1430},
                               new float[]{130, 210, 250, 180, 260, 150})
                .towels(new float[]{180, 920, 1420}, new float[]{175, 225, 195})
                .masks(new float[]{550, 1170}, new float[]{75, 305})
                .nextScene(SceneType.SCORE_BOARD.name())
                .displayName("Level 2 - The Escape")
                .build();
    }

    private static LevelConfig createLevel3() {
        return new Builder(3, 3000f, 900f)
                .timerDuration(30f)
                .playerMaxHp(80f)
                .npcMaxHp(50f)
                .playerStartX(200f)
                .playerStartY(40f)
                .exitX(1950f)
                .npcX(1000f)
                .npcY(190f)
                .groundSegments(40f, new float[]{0, 350, 750, 1100, 1500}, new float[]{250, 400, 350, 400, 500})
                .platforms(new float[]{300, 350, 620, 900, 1150, 1400, 1900},
                           new float[]{130, 210, 300, 180, 260, 150, 220},
                           new float[]{200, 220, 200, 180, 200, 250, 200})
                .groundFires(new float[]{150, 400, 440, 480, 520 , 600, 800, 1150,1200 ,1250, 1300, 1600, 1700})
                .ceilingFires(new float[]{280, 360, 750, 950, 1180, 1430},
                               new float[]{130, 210, 300, 180, 260, 150})
                .towels(new float[]{180,650, 920, 1420}, new float[]{175, 225, 195})
                .masks(new float[]{550, 1170}, new float[]{75, 305})
                .nextScene(SceneType.SCORE_BOARD.name())
                .displayName("Level 3 - The Escape")
                .build();
    }

    public static class Builder {
        private final int levelNumber;
        private final float worldWidth;
        private final float worldHeight;
        private float timerDuration = 60f;
        private float playerMaxHp = 100f;
        private float npcMaxHp = 100f;
        private float playerStartX = 200f;
        private float playerStartY = 0f;
        private float exitX;
        private float npcX = 500f;
        private float npcY = 0f;
        private float[] groundSegmentsX = {};
        private float[] groundSegmentsWidth = {};
        private float[] platformX = {};
        private float[] platformY = {};
        private float[] platformWidth = {};
        private float[] groundFireX = {};
        private float[] ceilingFireX = {};
        private float[] ceilingFireY = {};
        private float[] towelX = {};
        private float[] towelY = {};
        private float[] maskX = {};
        private float[] maskY = {};
        private String nextScene = "";
        private String displayName = "";

        public Builder(int levelNumber, float worldWidth, float worldHeight) {
            this.levelNumber = levelNumber;
            this.worldWidth = worldWidth;
            this.worldHeight = worldHeight;
        }

        public Builder timerDuration(float duration) { this.timerDuration = duration; return this; }
        public Builder playerMaxHp(float hp) { this.playerMaxHp = hp; return this; }
        public Builder npcMaxHp(float hp) { this.npcMaxHp = hp; return this; }
        public Builder playerStartX(float x) { this.playerStartX = x; return this; }
        public Builder playerStartY(float y) { this.playerStartY = y; return this; }
        public Builder exitX(float x) { this.exitX = x; return this; }
        public Builder npcX(float x) { this.npcX = x; return this; }
        public Builder npcY(float y) { this.npcY = y; return this; }
        public Builder groundSegments(float height, float[] x, float[] width) {
            this.groundSegmentsX = x; this.groundSegmentsWidth = width; return this;
        }
        public Builder platforms(float[] x, float[] y, float[] width) {
            this.platformX = x; this.platformY = y; this.platformWidth = width; return this;
        }
        public Builder groundFires(float[] x) { this.groundFireX = x; return this; }
        public Builder ceilingFires(float[] x, float[] y) { this.ceilingFireX = x; this.ceilingFireY = y; return this; }
        public Builder towels(float[] x, float[] y) { this.towelX = x; this.towelY = y; return this; }
        public Builder masks(float[] x, float[] y) { this.maskX = x; this.maskY = y; return this; }
        public Builder nextScene(String scene) { this.nextScene = scene; return this; }
        public Builder displayName(String name) { this.displayName = name; return this; }

        public LevelConfig build() { return new LevelConfig(this); }
    }
}
