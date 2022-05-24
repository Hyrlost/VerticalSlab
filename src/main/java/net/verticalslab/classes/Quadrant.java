package net.verticalslab.classes;

import java.util.Map;

public class Quadrant {
    public static Map<String, Boolean> Quadrants = Map.of(
            "Positive,Positive", false,
            "Negative,Positive", true,
            "Negative,Negative", false,
            "Positive,Negative", true
    );

    public static Map<String, VerticalSlabType> PositiveResult = Map.of(
            "Positive,Positive", VerticalSlabType.WEST,
            "Negative,Positive", VerticalSlabType.EAST,
            "Negative,Negative", VerticalSlabType.SOUTH,
            "Positive,Negative", VerticalSlabType.SOUTH
    );

    public static Map<String, VerticalSlabType> NegativeResult = Map.of(
            "Positive,Positive", VerticalSlabType.NORTH,
            "Negative,Positive", VerticalSlabType.NORTH,
            "Negative,Negative", VerticalSlabType.EAST,
            "Positive,Negative", VerticalSlabType.WEST
    );

    public static boolean IsSlopeHigher(double x, double z){
        z = z * - 1;
        try{
            double slope = (z + 50) / x;

            return slope > 1;
        }catch (Exception e){
            return true;
        }
    }

    public static boolean IsSlopeHigherReverted(double x, double z){
        z = z * - 1;
        try{
            double slope = z / x;

            return slope > -1;
        }catch (Exception e){
            return true;
        }
    }
}
