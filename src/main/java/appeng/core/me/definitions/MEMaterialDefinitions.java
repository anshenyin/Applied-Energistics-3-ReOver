
package appeng.core.me.definitions;


import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.IMaterialDefinition;
import appeng.core.AppEng;
import appeng.core.api.material.Material;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.me.api.definitions.IMEMaterialDefinitions;


public class MEMaterialDefinitions extends Definitions<Material, IMaterialDefinition<Material>> implements IMEMaterialDefinitions
{

	private static final String MATERIALSMODELSLOCATION = "material/";
	private static final String MATERIALSMODELSVARIANT = "inventory";

	public MEMaterialDefinitions( FeatureFactory registry )
	{
		init();
	}

}
