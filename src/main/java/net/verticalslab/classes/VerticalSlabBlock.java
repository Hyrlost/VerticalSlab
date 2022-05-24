package net.verticalslab.classes;

import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class VerticalSlabBlock extends Block implements Waterloggable {
    public static final EnumProperty<VerticalSlabType> TYPE;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape NORTH_SHAPE;
    protected static final VoxelShape SOUTH_SHAPE;
    protected static final VoxelShape WEST_SHAPE;
    protected static final VoxelShape EAST_SHAPE;

    public VerticalSlabBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.getDefaultState().with(TYPE, VerticalSlabType.NORTH)).with(WATERLOGGED, false));
    }

    public boolean hasSidedTransparency(BlockState state) {
        return state.get(TYPE) != VerticalSlabType.DOUBLE;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{TYPE, WATERLOGGED});
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VerticalSlabType slabType = (VerticalSlabType)state.get(TYPE);
        switch (slabType) {
            case DOUBLE:
                return VoxelShapes.fullCube();
            case EAST:
                return EAST_SHAPE;
            case WEST:
                return WEST_SHAPE;
            case NORTH:
                return NORTH_SHAPE;
            default:
                return SOUTH_SHAPE;
        }
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        BlockState blockState = ctx.getWorld().getBlockState(blockPos);
        if (blockState.isOf(this)) {
            return (BlockState)((BlockState)blockState.with(TYPE, VerticalSlabType.DOUBLE)).with(WATERLOGGED, false);
        }
        BlockState blockStateDefault = (BlockState)((BlockState)this.getDefaultState());
        Direction direction = ctx.getSide();

        switch (direction){
            case NORTH -> {
                return blockStateDefault.with(TYPE, VerticalSlabType.NORTH);
            }
            case SOUTH -> {
                return blockStateDefault.with(TYPE, VerticalSlabType.SOUTH);
            }
            case EAST -> {
                return blockStateDefault.with(TYPE, VerticalSlabType.EAST);
            }
            case WEST -> {
                return blockStateDefault.with(TYPE, VerticalSlabType.WEST);
            }
            default -> {
                VerticalSlabType newVerticalSlabType = GetBlockStateFromXandZ(ctx.getHitPos().x - (double)blockPos.getX(), ctx.getHitPos().z - (double)blockPos.getZ());
                return blockStateDefault.with(TYPE, newVerticalSlabType);
            }
        }
    }

    public VerticalSlabType GetBlockStateFromXandZ(double x, double z){
        double calculatingX = x;
        double calculatingZ = z;
        String quadrant = "";
        if (x > 0.5){
            quadrant = "Positive,";
            calculatingX = x - 0.5;
        } else {
            quadrant = "Negative,";
        }
        if (z > 0.5){
            quadrant = quadrant + "Positive";
            calculatingZ = z - 0.5;
        } else {
            quadrant = quadrant + "Negative";
        }

        Boolean IsNormalSlope = Quadrant.Quadrants.get(quadrant);
        Boolean IsHigher = IsNormalSlope ? Quadrant.IsSlopeHigher(calculatingX * 100, calculatingZ * 100) : Quadrant.IsSlopeHigherReverted(calculatingX * 100, calculatingZ * 100);
        VerticalSlabType newVerticalSlabType = IsHigher ? Quadrant.PositiveResult.get(quadrant) : Quadrant.NegativeResult.get(quadrant);

        return newVerticalSlabType;
    }



    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        ItemStack itemStack = context.getStack();
        VerticalSlabType verticalSlabType = (VerticalSlabType)state.get(TYPE);
        if (verticalSlabType != VerticalSlabType.DOUBLE && itemStack.isOf(this.asItem())) {
            if (context.canReplaceExisting()) {
                boolean bl = context.getHitPos().y - (double)context.getBlockPos().getY() > 0.5;
                Direction direction = context.getSide();
                if (verticalSlabType == VerticalSlabType.EAST) {
                    return direction == Direction.UP || bl && direction.getAxis().isVertical();
                } else {
                    return direction == Direction.DOWN || !bl && direction.getAxis().isVertical();
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        return state.get(TYPE) != VerticalSlabType.DOUBLE ? Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState) : false;
    }

    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return state.get(TYPE) != VerticalSlabType.DOUBLE ? Waterloggable.super.canFillWithFluid(world, pos, state, fluid) : false;
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if ((Boolean)state.get(WATERLOGGED)) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        switch (type) {
            case LAND:
                return false;
            case WATER:
                return world.getFluidState(pos).isIn(FluidTags.WATER);
            case AIR:
                return false;
            default:
                return false;
        }
    }

    static {
        TYPE = EnumProperty.of("type", VerticalSlabType.class);;
        WATERLOGGED = Properties.WATERLOGGED;
        //RIGHT_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 8.0, 16.0, 16.0);
        //LEFT_SHAPE = Block.createCuboidShape(8.0, 0.0, 0.0, 16.0, 16.0, 16.0);

        NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 8.0, 16.0, 16.0, 16.0);
        EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 8.0, 16.0, 16.0);
        SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 8.0);
        WEST_SHAPE = Block.createCuboidShape(8.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    }
}
