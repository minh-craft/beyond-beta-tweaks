package com.minhcraft.beyondbetatweaks.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class ModConfig extends MidnightConfig {

    public static final String GAMEPLAY = "gameplay";
    public static final String LIGHTING = "lighting";
    public static final String AESTHETICS = "aesthetics";

    @Entry(category = GAMEPLAY, isSlider = true, min=20, max=200)
    public static int giantMaxHealth = 80;

    @Entry(category = GAMEPLAY, isSlider = true, min=0d, max=3d)
    public static double giantSpeedModifier = 1.2;

    @Entry(category = GAMEPLAY, isSlider = true, min=0, max=50, precision = 1)
    public static double giantAttackDamage = 10.0;

    @Entry(category = GAMEPLAY, isSlider = true, min=0, max=20)
    public static int cakeMaxAbsorptionHearts = 8;

    @Entry(category = GAMEPLAY, isSlider = true, min=0f, max=1.5f)
    public static float skeletonArrowVelocityDecrease = 0.5f;

    @Entry(category = GAMEPLAY, isSlider = true, min=-1f, max=1f)
    public static float spiderBoundingBoxAttackRangeIncrease = 0f;

    @Entry(category = GAMEPLAY, isSlider = true, min=-1f, max=1f)
    public static float caveSpiderBoundingBoxAttackRangeIncrease = 0f;

    @Entry(category = GAMEPLAY, isSlider = true, min=0f, max=1f)
    public static float zombieBoundingBoxAttackRangeIncrease = 0.3f;

    @Entry(category = GAMEPLAY, isSlider = true, min=-3f, max=1f)
    public static float giantBoundingBoxAttackRangeIncrease = -1.3f;

    @Entry(category = GAMEPLAY)
    public static boolean giantDespawnInSunlight = true;

    @Entry(category = GAMEPLAY, isSlider = true, min=1, max=11)
    public static int giantDespawnWhenSkyDarkenLessThan = 9;

    @Entry(category = GAMEPLAY, isSlider = true, min=0d, max=1d)
    public static double swordRangeIncrease = 0.0d;

    @Entry(category = GAMEPLAY)
    public static boolean disableStraySlowArrows = true;

    @Entry(category = GAMEPLAY)
    public static boolean disableOcelotAvoidingPlayer = false;

    @Entry(category = GAMEPLAY, isSlider = true, min=0f, max=16f, precision = 10)
    public static float ocelotAvoidMaxDistance = 8.0f;

    @Entry(category = GAMEPLAY, isSlider = true, min=50, max=5000)
    public static int endGatewayRadius = 96;

    @Entry(category = GAMEPLAY, isSlider = true, min=0, max=256)
    public static int endGatewayHeight = 75;

    @Entry(category = LIGHTING, isSlider = true, min=0.0F, max=1.0F)
    public static float fullMoonBrightness = 0.93F;

    @Entry(category = LIGHTING, isSlider = true, min=0.0F, max=1.0F)
    public static float threeQuartersMoonBrightness = 0.86F;

    @Entry(category = LIGHTING, isSlider = true, min=0.0F, max=1.0F)
    public static float halfMoonBrightness = 0.68F;

    @Entry(category = LIGHTING, isSlider = true, min=0.0F, max=1.0F)
    public static float oneQuarterMoonBrightness = 0.5F;

    @Entry(category = LIGHTING, isSlider = true, min=0.0F, max=1.0F)
    public static float newMoonBrightness = 0.0F;

    @Entry(category = LIGHTING, isSlider = true, min=0.0F, max=1.0F)
    public static float nightVisionModifier = 0.25F;

    @Entry(category = LIGHTING, isSlider = true, min=1.0F, max=10.0F, precision = 10)
    public static float endDimensionTrueDarknessBrightnessScaling = 1.0F;

    @Entry(category = LIGHTING)
    public static boolean enableEndDimensionLightmapGradientFix = true;

    @Entry(category = LIGHTING, isSlider = true, min=0.0F, max=1.0F)
    public static float endDimensionLightmapGradientSmoothingFactor = 0.75F;

    @Entry(category = LIGHTING, isSlider = true, min=0.0F, max=0.2F, precision = 1000)
    public static float trueDarknessMinimumLightLevel = 0.03F;

    @Entry(category = LIGHTING, isSlider = true, min=1.0F, max=5.0F)
    public static float scaleTrueDarknessGamma = 3.0F;

    @Entry(category = LIGHTING)
    public static boolean enableNightSkylightLightTextureBoost = true;

    @Entry(category = LIGHTING, isSlider = true, min = 0.0F, max=10.0F)
    public static float boostNightBrightnessFactor = 1.15F;

    @Entry(category = LIGHTING, isSlider = true, min = 0.0F, max=200.0F)
    public static float maxPossibleBoostedNightBrightness = 120.0F;

    @Entry(category = LIGHTING, isSlider = true, min=0.0F, max=1.0F)
    public static float fullMoonBrightnessBoost = 1.0F;

    @Entry(category = LIGHTING, isSlider = true, min=0.0F, max=1.0F)
    public static float threeQuartersMoonBrightnessBoost = 0.86F;

    @Entry(category = LIGHTING, isSlider = true, min=0.0F, max=1.0F)
    public static float halfMoonBrightnessBoost = 0.58F;

    @Entry(category = LIGHTING, isSlider = true, min=0.0F, max=1.0F)
    public static float oneQuarterMoonBrightnessBoost = 0.31F;

    @Entry(category = LIGHTING, isSlider = true, min=0.0F, max=1.0F)
    public static float newMoonBrightnessBoost = 0.0F;

    @Entry(category = LIGHTING)
    public static boolean enableDynamicLightBrightnessAffectBlockLight = true;

    @Entry(category = LIGHTING)
    public static boolean dynamicLightBrightnessBlockLightGammaReductionSquared = true;

    @Entry(category = LIGHTING, isSlider = true, min=0.0F, max=1.0F)
    public static float dynamicLightBrightnessBlockLightScale = 0.15F;

    @Entry(category = GAMEPLAY, isSlider = true, min=1.0F, max=10.0F)
    public static float crossbowShootingPower = 4.59F;

    @Entry(category = GAMEPLAY, isSlider = true, min=10, max=50)
    public static int crossbowChargeDuration = 30;

    @Entry(category = GAMEPLAY, isSlider = true, min=0.0, max=4.0)
    public static double crossbowArrowBaseDamage = 2.35;

    @Entry(category = AESTHETICS)
    public static boolean disableArcheryExpansionArrowGui = false;

    @Entry(category = GAMEPLAY, isSlider = true, min=0.0, max=1.0)
    public static double screamingGoatChance = 0.25;

    @Entry(category = GAMEPLAY, isSlider = true, min=1, max=30)
    public static int regularGoatMinimumRamWaitTimeSeconds = 30;

    @Entry(category = GAMEPLAY, isSlider = true, min=1, max=300)
    public static int regularGoatMaximumRamWaitTimeSeconds = 240;

    @Entry(category = GAMEPLAY, isSlider = true, min=1, max=30)
    public static int screamingGoatMinimumRamWaitTimeSeconds = 15;

    @Entry(category = GAMEPLAY, isSlider = true, min=1, max=300)
    public static int screamingGoatMaximumRamWaitTimeSeconds = 120;

    @Entry(category = GAMEPLAY, isSlider = true, min=0.0, max=1.0, precision = 1000)
    public static double boatMaxGroundSlipperiness = 0.9;

    @Entry(category = GAMEPLAY, isSlider = true, min=0.0F, max=0.9F, precision = 1000)
    public static float boatWaterSlipperiness = 0.9F;

    @Entry(category = GAMEPLAY, isSlider = true, min=0.0F, max=5.0F)
    public static float lavaBoatMaxYawVelocity = 1.85F;

    @Entry(category = GAMEPLAY, isSlider = true, min=0.0F, max=0.5F)
    public static float lavaBoatMaxHorizontalSpeed = 0.16F;

    @Entry(category = GAMEPLAY, min=0.0F, max=5.0)
    public static float enderpearlTeleportDamage = 2.0F;

    @Entry(category = GAMEPLAY, isSlider = true, min=1, max=20)
    public static int ambientWaterMobCap = 10;

    @Entry(category = GAMEPLAY)
    public static int monsterMobCapOverworld = 90;

    @Entry(category = GAMEPLAY)
    public static int monsterMobCapNether = 60;

    @Entry(category = GAMEPLAY)
    public static int monsterMobCapTheEnd = 30;

    @Entry(category = GAMEPLAY, isSlider = true, min=0F, max=0.175F, precision = 1000)
    public static float donkeyMovementSpeed = 0.165F;

    @Entry(category = GAMEPLAY, isSlider = true, min=0d, max=0.3375d, precision = 1000)
    public static double horseMovementSpeed = 0.16;

    @Entry(category = GAMEPLAY, isSlider = true, min=200.0, max=1024.0, precision = 1)
    public static double endGatewayTeleportDistance = 1024.0;

    @Entry(category = GAMEPLAY)
    public static boolean enableScaffoldingLikeLadderPlacement = true;

    @Entry(category = GAMEPLAY, isSlider = true, min=1, max=256)
    public static int ladderScaffoldingMaxDistance = 16;

    @Entry(category = AESTHETICS)
    public static boolean alwaysShowScholarFormattingToolbar = true;
  
    @Entry(category = AESTHETICS)
    public static boolean disableMelancholicHungerFoodRegenerationSpeedTooltip = true;

    @Entry(category = AESTHETICS, isSlider = true, min=0.1F, max=1.0F)
    public static float spyglassDefaultZoom = 0.3F;

    @Entry(category = AESTHETICS)
    public static boolean enableMoonPhaseAffectCloudColor = true;

    @Entry(category = AESTHETICS, isSlider = true, min=0.0F, max=0.3F, precision = 1000)
    public static float whitenedCloudFullMoonBrightness = 0.14F;

    @Entry(category = AESTHETICS, isSlider = true, min=0.0F, max=0.3F, precision = 1000)
    public static float whitenedCloudNewMoonBrightness = 0.03F;

    @Entry(category = AESTHETICS)
    public static boolean enableMoonPhaseAffectFogColor = true;

    @Entry(category = AESTHETICS)
    public static String nightFogColorFullMoon = "#010F19";

    @Entry(category = AESTHETICS)
    public static String nightFogColorNewMoon = "#000000";

    @Entry(category = GAMEPLAY)
    public static boolean disableCrawling = true;

    @Entry(category = GAMEPLAY, min=0)
    public static int oldAnimalRespawnTickInterval = 1200;

    @Entry(category = GAMEPLAY)
    public static boolean makeRespawningAnimalsPersistent = true;

    @Entry(category = GAMEPLAY, min=1)
    public static int animalBreedingCooldownInSeconds = 60 * 20 * 3;

    @Entry(category = GAMEPLAY, min=1)
    public static int chickenLayEggsCooldownInSeconds = 60 * 19;

    @Entry(category = GAMEPLAY, min=1)
    public static int babyAnimalGrowUpTimeInSeconds = 60 * 20 *3;

    @Entry(category = GAMEPLAY, isSlider = true, min=1, max=8)
    public static int maxEggsPerLay = 3;

    @Entry(category = GAMEPLAY, isSlider = true, min=1, max=8)
    public static int maxFeathersPerShed=3;

    @Entry(category = GAMEPLAY, min=1)
    public static int chickenShedFeathersWaitBaseTimeInSeconds = 60 * 4;

    @Entry(category = GAMEPLAY, min=1)
    public static int chickenShedFeathersWaitMaxAdditionalTimeInSeconds = 60 * 4;

    @Entry(category = GAMEPLAY, isSlider = true, min=0.0F, max=1.0F)
    public static float chickenFromEggChance = 0.33F;

    @Entry(category = GAMEPLAY)
    public static String pigLitterSizing = "50:1,30:2,20:3";

    @Entry(category = AESTHETICS)
    public static boolean shrinkBabyHoglinHead = true;

    public enum FlatLightingMode {
        AVERAGE,
        MINIMUM,
        MAXIMUM
    }

    @Entry(category = LIGHTING)
    public static boolean enableFlatLightingWithAmbientOcclusion = true;

    @Entry(category = LIGHTING)
    public static FlatLightingMode flatLightingMode = FlatLightingMode.MINIMUM;

    @Entry(category = AESTHETICS, isSlider = true, min=0, max=200)
    public static int noFallDamageSoundLoginGracePeriodInTicks = 80;

    @Entry(category = LIGHTING, isSlider = true, min=0.0F, max=1.0F)
    public static float oldSmoothLightingMinimumShadeDarkness = 0.07F;

    @Entry(category = AESTHETICS, isSlider = true, min=0.0F, max=1.0F)
    public static float bannerWaviness = 0.25F;

    @Entry(category = AESTHETICS, isSlider = true, min=0.0F, max=0.5F)
    public static float endDimensionBaseStarSize = 0.22F;

    @Entry(category = AESTHETICS, isSlider = true, min=0.0F, max=0.5F)
    public static float endDimensionStarSizeVariation = 0.2F;

    @Entry(category = AESTHETICS, isColor = true)
    public static String endDimensionSkyColor = "#040408";

    @Entry(category = AESTHETICS, isColor = true)
    public static String endDimensionFogColor = "#12121f";

    @Entry(category = AESTHETICS, isColor = true)
    public static String endDimensionFogUpperColor = "#1c1626";

    @Entry(category = AESTHETICS, isSlider = true, min=-20.0, max=20.0, precision = 1)
    public static double endDimensionStarFadeTopAngle = 3;

    @Entry(category = AESTHETICS, isSlider = true, min=-20.0, max=20.0)
    public static double endDimensionStarFadeBottomAngle = -7;

    @Entry(category = GAMEPLAY, min=0L, max=24000)
    public static int firstTimeEndExitPortalFastForwardToTargetTime = 23500;

    @Entry(category = GAMEPLAY, isSlider = true, min=0, max=50)
    public static int bedrockEggVegetationClearingHeight = 30;

    @Entry(category = GAMEPLAY, isSlider = true, min=0, max=3)
    public static int bedrockEggHeightScanningRadius = 1;

    @Entry(category = AESTHETICS)
    public static boolean allowVanillaCloudsToCoexistWithCloudLayersClouds = true;

    @Entry(category = AESTHETICS, isSlider = true, min=0.0F, max=1.0F)
    public static float endFlashLightingIntensity = 0.4F;

    @Entry(category = AESTHETICS, min=0, max=255)
    public static int endFlashLightRed = 43;

    @Entry(category = AESTHETICS, min=0, max=255)
    public static int endFlashLightGreen = 32;

    @Entry(category = AESTHETICS, min=0, max=255)
    public static int endFlashLightBlue = 46;

    @Entry(category = AESTHETICS, min=0)
    public static int endFlashMinimumDelayInTicks = 100;

    @Entry(category = AESTHETICS, min=0)
    public static int endFlashMaximumDelayInTicks = 600;

    @Entry(category = AESTHETICS, isSlider = true, min = -90, max = 90)
    public static int endFlashMinimumElevationAngle = -90;

    @Entry(category = AESTHETICS, isSlider = true, min = -90, max = 90)
    public static int endFlashMaximumElevationAngle = -70;

    @Entry(category = AESTHETICS, isSlider = true, min=0.0F, max=1.0F)
    public static float endFlashVolume = 1.0F;

    @Entry(category = AESTHETICS)
    public static boolean enableEndFog = true;

    @Entry(category = AESTHETICS, isSlider = true, min=0.0F, max=1.0F)
    public static float endTerrainFogStart = 0.4F;

    @Entry(category = LIGHTING)
    public static boolean enableTrueDarknessNetherLightingAdjustments = true;

    @Entry(category = LIGHTING, isSlider = true, min=0.0F, max=1.0F)
    public static float netherDimensionTrueDarknessLevel = 1.0F;

    @Entry(category = LIGHTING, isSlider = true, min=0.0F, max=1.0F)
    public static float netherLightmapSmoothingFactor = 0.5F;

    @Entry(category = LIGHTING)
    public static boolean enableReducedLeavesAmbientOcclusion = true;

    @Entry(category = GAMEPLAY, isSlider = true, min = -64, max = -40)
    public static int overworldLavaLevel = -53;

    @Entry(category = LIGHTING, isSlider = true, min = 0, max = 15)
    public static int glowLichenAirExposedLight = 7;

    @Entry(category = LIGHTING, isSlider = true, min = 0, max = 15)
    public static int glowLichenUnderwaterLight = 12;

    @Entry(category = LIGHTING)
    public static boolean enableOverworldLightmapSmoothing = false;

    @Entry(category = LIGHTING, isSlider = true, min=0.0F, max=1.0F)
    public static float overworldLightmapGradientSmoothingFactor = 0.75F;
}
