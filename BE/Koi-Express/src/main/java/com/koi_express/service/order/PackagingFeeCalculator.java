package com.koi_express.service.order;

public class PackagingFeeCalculator {

    private static final int SMALL_FISH_FEE = 15000; // VND per fish for fish < 30 cm
    private static final int MEDIUM_BOX_FEE = 300000; // VND per box for fish between 30-50 cm
    private static final int LARGE_FISH_FEE = 300000; // VND per fish for fish > 50 cm
    private static final int MEDIUM_BOX_CAPACITY = 4; // 4 fish per box for 30-50 cm
    private static final int LARGE_TANK_CAPACITY = 13; // 13 fish per tank for fish > 50 cm

    public int calculateTotalPackagingFee(int[] fishSize) {
        int totalFee =0;
        int smallFishCount = 0;
        int mediumFishCount = 0;
        int largeFishCount = 0;

        for (int size : fishSize) {
            if (size <= 0) {
                continue; // Ignore fish with size 0
            } else if (size < 30) {
                smallFishCount++;
            } else if (size >= 30 && size <= 50) {
                mediumFishCount++;
            } else if (size > 50) {
                largeFishCount++;
            }
        }

        totalFee += smallFishCount * SMALL_FISH_FEE;

        int mediumBoxCount = (int) Math.ceil((double) mediumFishCount / MEDIUM_BOX_CAPACITY);
        totalFee += mediumBoxCount * MEDIUM_BOX_FEE;

        totalFee += largeFishCount * LARGE_FISH_FEE;

        return totalFee;
    }
}
