package com.minhcraft.beyondbetatweaks.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class ModConfig extends MidnightConfig {

    @Entry(isSlider = true, min=20, max=200)
    public static int giantMaxHealth = 80;

    @Entry(isSlider = true, min=0d, max=3d)
    public static double giantSpeedModifier = 1.2;

    @Entry(isSlider = true, min=0, max=50, precision = 1)
    public static double giantAttackDamage = 10.0;

    @Entry(isSlider = true, min=0, max=20)
    public static int cakeMaxAbsorptionHearts = 8;

    @Entry(isSlider = true, min=0f, max=1.5f)
    public static float skeletonArrowVelocityDecrease = 0.5f;

    @Entry(isSlider = true, min=-1f, max=1f)
    public static float spiderBoundingBoxAttackRangeIncrease = 0f;

    @Entry(isSlider = true, min=-1f, max=1f)
    public static float caveSpiderBoundingBoxAttackRangeIncrease = 0f;

    @Entry(isSlider = true, min=0f, max=1f)
    public static float zombieBoundingBoxAttackRangeIncrease = 0.3f;

    @Entry(isSlider = true, min=-3f, max=1f)
    public static float giantBoundingBoxAttackRangeIncrease = -1.3f;

    @Entry
    public static boolean giantDespawnInSunlight = true;

    @Entry(isSlider = true, min=1, max=11)
    public static int giantDespawnWhenSkyDarkenLessThan = 9;

    @Entry(isSlider = true, min=0d, max=1d)
    public static double swordRangeIncrease = 0.0d;

    @Entry
    public static boolean disableStraySlowArrows = true;

    @Entry
    public static boolean disableOcelotAvoidingPlayer = false;

    @Entry(isSlider = true, min=0f, max=16f, precision = 10)
    public static float ocelotAvoidMaxDistance = 8.0f;

    @Entry(isSlider = true, min=50, max=5000)
    public static int endGatewayRadius = 96;

    @Entry(isSlider = true, min=0, max=256)
    public static int endGatewayHeight = 75;

    @Entry(isSlider = true, min=0.0F, max=1.0F)
    public static float fullMoonBrightness = 0.93F;

    @Entry(isSlider = true, min=0.0F, max=1.0F)
    public static float threeQuartersMoonBrightness = 0.86F;

    @Entry(isSlider = true, min=0.0F, max=1.0F)
    public static float halfMoonBrightness = 0.68F;

    @Entry(isSlider = true, min=0.0F, max=1.0F)
    public static float oneQuarterMoonBrightness = 0.5F;

    @Entry(isSlider = true, min=0.0F, max=1.0F)
    public static float newMoonBrightness = 0.0F;

    @Entry(isSlider = true, min=0.0F, max=1.0F)
    public static float nightVisionModifier = 0.25F;

    @Entry(isSlider = true, min=0.0F, max=2.0F)
    public static float endDimensionLightMapBrightnessModifier = 0.5F;

    @Entry(isSlider = true, min=0.0F, max=0.2F, precision = 1000)
    public static float trueDarknessMinimumLightLevel = 0.03F;

    @Entry(isSlider = true, min=0, max=11)
    public static int roundRobinMaximumDeductedLightLevel = 8;

    @Entry(isSlider = true, min=1.0F, max=4.0F)
    public static float scaleTrueDarknessGamma = 3.0F;

    @Entry
    public static boolean enableDynamicLightBrightnessAffectBlockLight = true;

    @Entry
    public static boolean dynamicLightBrightnessBlockLightGammaReductionSquared = true;

    @Entry(isSlider = true, min=0.0F, max=1.0F)
    public static float dynamicLightBrightnessBlockLightScale = 0.15F;

    @Entry(isSlider = true, min=1.0F, max=10.0F)
    public static float crossbowShootingPower = 4.59F;

    @Entry(isSlider = true, min=10, max=50)
    public static int crossbowChargeDuration = 30;

    @Entry(isSlider = true, min=0.0, max=4.0)
    public static double crossbowArrowBaseDamage = 2.35;

    @Entry
    public static boolean disableArcheryExpansionArrowGui = false;

    @Entry(isSlider = true, min=0.0, max=1.0)
    public static double screamingGoatChance = 0.25;

    @Entry(isSlider = true, min=1, max=30)
    public static int regularGoatMinimumRamWaitTimeSeconds = 30;

    @Entry(isSlider = true, min=1, max=300)
    public static int regularGoatMaximumRamWaitTimeSeconds = 240;

    @Entry(isSlider = true, min=1, max=30)
    public static int screamingGoatMinimumRamWaitTimeSeconds = 15;

    @Entry(isSlider = true, min=1, max=300)
    public static int screamingGoatMaximumRamWaitTimeSeconds = 120;

    @Entry(isSlider = true, min=0.0, max=1.0, precision = 1000)
    public static double boatMaxGroundSlipperiness = 0.9;

    @Entry(isSlider = true, min=0.0F, max=0.9F, precision = 1000)
    public static float boatWaterSlipperiness = 0.9F;

    @Entry(min=0.0F, max=5.0)
    public static float enderpearlTeleportDamage = 2.0F;

    @Entry(isSlider = true, min=1, max=20)
    public static int ambientWaterMobCap = 10;

    @Entry
    public static int monsterMobCapOverworld = 90;

    @Entry
    public static int monsterMobCapNether = 60;

    @Entry
    public static int monsterMobCapTheEnd = 30;

    @Entry(isSlider = true, min=0F, max=0.175F, precision = 1000)
    public static float donkeyMovementSpeed = 0.165F;

    @Entry(isSlider = true, min=0d, max=0.3375d, precision = 1000)
    public static double horseMovementSpeed = 0.16;

    @Entry(isSlider = true, min=512.0, max=1024.0, precision = 1)
    public static double endGatewayTeleportDistance = 1024.0;

    @Entry
    public static boolean enableScaffoldingLikeLadderPlacement = true;

    @Entry(isSlider = true, min=1, max=256)
    public static int ladderScaffoldingMaxDistance = 16;

    @Entry
    public static boolean alwaysShowScholarFormattingToolbar = true;
  
    @Entry
    public static boolean disableMelancholicHungerFoodRegenerationSpeedTooltip = true;

    @Entry(isSlider = true, min=0.1F, max=1.0F)
    public static float spyglassDefaultZoom = 0.3F;

    @Entry
    public static boolean enableMoonPhaseAffectCloudColor = true;

    @Entry(isSlider = true, min=0.0F, max=0.3F, precision = 1000)
    public static float whitenedCloudFullMoonBrightness = 0.14F;

    @Entry(isSlider = true, min=0.0F, max=0.3F, precision = 1000)
    public static float whitenedCloudNewMoonBrightness = 0.03F;

    @Entry
    public static boolean enableMoonPhaseAffectFogColor = true;

    @Entry
    public static String nightFogColorFullMoon = "#010F19";

    @Entry
    public static String nightFogColorNewMoon = "#000000";
}
