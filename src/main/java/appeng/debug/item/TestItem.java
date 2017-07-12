package appeng.debug.item;

import appeng.core.skyfall.AppEngSkyfall;
import appeng.core.skyfall.api.definitions.ISkyfallBlockDefinitions;
import appeng.core.skyfall.block.CertusInfusedBlock;
import appeng.core.skyfall.config.SkyfallConfig;
import code.elix_x.excore.utils.world.MutableBlockAccess;
import code.elix_x.excore.utils.world.MutableBlockAccessWorldDelegate;
import code.elix_x.excore.utils.world.OriginTransformingMutableBlockAccess;
import code.elix_x.excore.utils.world.TransformingMutableBlockAccess;
import hall.collin.christopher.math.noise.DefaultFractalNoiseGenerator3D;
import hall.collin.christopher.math.noise.SphericalSurfaceFractalNoiseGenerator;
import hall.collin.christopher.math.random.DefaultRandomNumberGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TestItem extends Item {

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World oldworld, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(oldworld.isRemote) return EnumActionResult.SUCCESS;
		MutableBlockAccess wworld = new MutableBlockAccessWorldDelegate(oldworld);
		SkyfallConfig.Meteorite config = AppEngSkyfall.INSTANCE.config.meteorite;
		Random random = new Random();
		float radius = RandomUtils.nextFloat(config.minRadius, /*config.maxRadius*/ 35);
		List<IBlockState> allowed = new ArrayList<>(config.getAllowedBlockStates());
		Collections.shuffle(allowed, random);
		int count = RandomUtils.nextInt(Math.min(allowed.size(), 2), allowed.size() + 1);
		for(int i = 0; i < count; i++){
			IBlockState block = allowed.get(i);
			Random localRandom = new Random(random.nextLong());
			float localRadius = RandomUtils.nextFloat(radius * 0.75f, radius * 1.25f);
			float radiusX = localRadius - localRadius * 0.25f + RandomUtils.nextFloat(0, localRadius * 0.5f);
			float radiusY = localRadius - localRadius * 0.25f + RandomUtils.nextFloat(0, localRadius * 0.5f);
			float radiusZ = localRadius - localRadius * 0.25f + RandomUtils.nextFloat(0, localRadius * 0.5f);
			float corruption = RandomUtils.nextFloat(0.65f, 0.95f);
			SphericalSurfaceFractalNoiseGenerator noiseGenerator = new SphericalSurfaceFractalNoiseGenerator(localRandom.nextLong());
			TransformingMutableBlockAccess world = new OriginTransformingMutableBlockAccess(wworld, pos.add(0, localRadius * 1.5, 0));
			{
				for(float x = -localRadius * 1.5f; x < localRadius * 1.5f; x++){
					for(float y = -localRadius * 1.5f; y < localRadius * 1.5f; y++){
						for(float z = -localRadius * 1.5f; z < localRadius * 1.5f; z++){
							BlockPos next = new BlockPos(x, y, z);
							if(x * x / (radiusX * radiusX) + y * y / (radiusY * radiusY) + z * z / (radiusZ * radiusZ) <= 1f + noiseGenerator.valueAt(1f / (2f * localRadius) , Math.atan(y/x), Math.acos(z/Math.sqrt(x*x+y*y+z*z))))
								if(localRandom.nextFloat() < corruption) world.setBlockState(next, block);
						}
					}
				}
			}
		}
		AppEngSkyfall.INSTANCE.<Block, ISkyfallBlockDefinitions>definitions(Block.class).certusInfused().maybe().ifPresent(certusInfusedBlock -> {
			Random localRandom = new Random(random.nextLong());
			DefaultFractalNoiseGenerator3D infusionNoise = new DefaultFractalNoiseGenerator3D(500, 0.3, 0.9, 1, new DefaultRandomNumberGenerator(localRandom.nextLong()));
			TransformingMutableBlockAccess world = new OriginTransformingMutableBlockAccess(wworld, pos.add(0, radius * 1.5, 0));
			final double p = 0.02;
			final double s = 0.05;
			final double threshold = 0.85;
			float corruption = RandomUtils.nextFloat(0.65f, 0.95f);
			for(float x = -radius * 1.5f; x < radius * 1.5f; x++){
				for(float y = -radius * 1.5f; y < radius * 1.5f; y++){
					for(float z = -radius * 1.5f; z < radius * 1.5f; z++){
						BlockPos next = new BlockPos(x, y, z);
						if(infusionNoise.valueAt(p, x * s, y * s, z * s) >= threshold)
							if(localRandom.nextFloat() < corruption)
								if(config.isAllowedState(world.getBlockState(next)))
									world.setBlockState(next, certusInfusedBlock.getStateFromMeta(CertusInfusedBlock.getStateVariant(world.getBlockState(next))));
					}
				}
			}
		});
		return EnumActionResult.SUCCESS;
	}

}