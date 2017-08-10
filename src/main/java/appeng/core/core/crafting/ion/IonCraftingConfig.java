package appeng.core.core.crafting.ion;

import appeng.api.bootstrap.InitializationComponent;
import appeng.api.config.ConfigCompilable;
import appeng.core.AppEng;
import appeng.core.core.AppEngCore;
import appeng.core.core.api.crafting.ion.Ion;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class IonCraftingConfig implements ConfigCompilable, InitializationComponent.Init  {

	private Map<String, List<MutablePair<String, Integer>>> oreDictToIons = new HashMap<>();
	public transient Multimap<String, Pair<Ion, Integer>> oreDictToIonsM = HashMultimap.create();

	public IonCraftingConfig(){
		oreDictToIons.put("gemQuartz", Lists.newArrayList(new MutablePair<>(new ResourceLocation(AppEng.MODID, "quartz").toString(), 1)));
		oreDictToIons.put("dustRedstone", Lists.newArrayList(new MutablePair<>(new ResourceLocation(AppEng.MODID, "redstone").toString(), 1)));
		oreDictToIons.put("gunpowder", Lists.newArrayList(new MutablePair<>(new ResourceLocation(AppEng.MODID, "sulfur").toString(), 1)));
		oreDictToIons.put("dustSulfur", Lists.newArrayList(new MutablePair<>(new ResourceLocation(AppEng.MODID, "sulfur").toString(), 1)));
		oreDictToIons.put("enderpearl", Lists.newArrayList(new MutablePair<>(new ResourceLocation(AppEng.MODID, "ender").toString(), 1)));

		oreDictToIons.put("certusQuartz", Lists.newArrayList(new MutablePair<>(new ResourceLocation(AppEng.MODID, "certus").toString(), 1), new MutablePair<>(new ResourceLocation(AppEng.MODID, "quartz").toString(), 3)));
		oreDictToIons.put("certusRedstone", Lists.newArrayList(new MutablePair<>(new ResourceLocation(AppEng.MODID, "certus").toString(), 1), new MutablePair<>(new ResourceLocation(AppEng.MODID, "redstone").toString(), 3)));
		oreDictToIons.put("certusSulfur", Lists.newArrayList(new MutablePair<>(new ResourceLocation(AppEng.MODID, "certus").toString(), 1), new MutablePair<>(new ResourceLocation(AppEng.MODID, "sulfur").toString(), 2)));
		oreDictToIons.put("incertus", Lists.newArrayList(new MutablePair<>(new ResourceLocation(AppEng.MODID, "certus").toString(), 1), new MutablePair<>(new ResourceLocation(AppEng.MODID, "ender").toString(), 1)));
	}

	@Override
	public void compile(){

	}

	@Override
	public void init(){
		oreDictToIonsM.clear();
		oreDictToIons.forEach((ore, ions) -> ions.forEach(ionr -> Optional.ofNullable(AppEngCore.INSTANCE.getIonRegistry().getValue(new ResourceLocation(ionr.getKey()))).ifPresent(ion -> oreDictToIonsM.put(ore, new ImmutablePair<>(ion, ionr.getRight())))));
	}

	@Override
	public void decompile(){
		oreDictToIons.clear();
		oreDictToIonsM.keySet().forEach(ore -> oreDictToIons.put(ore, oreDictToIonsM.get(ore).stream().map(ion -> new MutablePair<>(ion.getLeft().getRegistryName().toString(), ion.getRight())).collect(Collectors.toList())));
	}

}
