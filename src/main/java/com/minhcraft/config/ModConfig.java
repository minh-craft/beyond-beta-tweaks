package com.minhcraft.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class ModConfig extends MidnightConfig {

    @Entry(isSlider = true, min=20, max=200)
    public static int giantMaxHealth = 45;

    @Entry(isSlider = true, min=0d, max=3d)
    public static double giantSpeedModifier = 1.2;

    @Entry(isSlider = true, min=0, max=50, precision = 1)
    public static double giantAttackDamage = 8.0;

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
    public static int giantDespawnWhenSkyDarkenLessThan = 11;

    @Entry(isSlider = true, min=0d, max=1d)
    public static double swordRangeIncrease = 0.5d;

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
    public static float fullMoonBrightness = 1.0F;

    @Entry(isSlider = true, min=0.0F, max=1.0F)
    public static float threeQuartersMoonBrightness = 0.88F;

    @Entry(isSlider = true, min=0.0F, max=1.0F)
    public static float halfMoonBrightness = 0.75F;

    @Entry(isSlider = true, min=0.0F, max=1.0F)
    public static float oneQuarterMoonBrightness = 0.5F;

    @Entry(isSlider = true, min=0.0F, max=1.0F)
    public static float newMoonBrightness = 0.0F;

    @Entry(isSlider = true, min=0.0F, max=1.0F)
    public static float nightVisionModifier = 0.25F;

    @Entry(isSlider = true, min=0.0F, max=2.0F)
    public static float endDimensionLightMapBrightnessModifier = 0.8F;

    @Entry(isSlider = true, min=0.0F, max=0.2F, precision = 1000)
    public static float trueDarknessMinimumLightLevel = 0.01F;

    @Entry(isSlider = true, min=0, max=11)
    public static int roundRobinMaximumDeductedLightLevel = 11;

    @Entry(isSlider = true, min=1.0F, max=3.0F)
    public static float scaleTrueDarknessGamma = 1.0F;

    @Entry(isSlider = true, min=1.0F, max=10.0F)
    public static float crossbowShootingPower = 3.15F;

    @Entry(isSlider = true, min=10, max=50)
    public static int crossbowChargeDuration = 25;

    @Entry(isSlider = true, min=0.0, max=4.0)
    public static double crossbowArrowBaseDamage = 2.0;

    @Entry
    public static boolean disableArcheryExpansionArrowGui = false;

    @Entry(isSlider = true, min=0.0, max=1.0)
    public static double screamingGoatChance = 0.02;

    @Entry(isSlider = true, min=1, max=30)
    public static int regularGoatMinimumRamWaitTimeSeconds = 30;

    @Entry(isSlider = true, min=1, max=300)
    public static int regularGoatMaximumRamWaitTimeSeconds = 300;

    @Entry(isSlider = true, min=1, max=30)
    public static int screamingGoatMinimumRamWaitTimeSeconds = 5;

    @Entry(isSlider = true, min=1, max=300)
    public static int screamingGoatMaximumRamWaitTimeSeconds = 15;

    @Entry(isSlider = true, min=0.0, max=1.0, precision = 1000)
    public static double boatMaxGroundSlipperiness = 0.9;

    @Entry(isSlider = true, min=0.0F, max=0.9F, precision = 1000)
    public static float boatWaterSlipperiness = 0.9F;

    @Entry(min=0.0F, max=5.0)
    public static float enderpearlTeleportDamage = 2.0F;

    @Entry(isSlider = true, min=1, max=20)
    public static int ambientWaterMobCap = 10;

    @Entry
    public static int monsterMobCapOverworld = 70;

    @Entry
    public static int monsterMobCapNether = 70;

    @Entry
    public static int monsterMobCapTheEnd = 70;

    @Entry(isSlider = true, min=0F, max=0.175F, precision = 1000)
    public static float donkeyMovementSpeed = 0.14F;

    @Entry(isSlider = true, min=0d, max=0.3375d, precision = 1000)
    public static double horseMovementSpeed = 0.16;

    @Entry(isSlider = true, min=512.0, max=1024.0, precision = 1)
    public static double endGatewayTeleportDistance = 1024.0;
}
