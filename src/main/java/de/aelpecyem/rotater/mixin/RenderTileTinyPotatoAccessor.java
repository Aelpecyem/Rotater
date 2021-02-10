package de.aelpecyem.rotater.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import vazkii.botania.client.core.helper.ShaderHelper;
import vazkii.botania.client.render.tile.RenderTileTinyPotato;

@Mixin(RenderTileTinyPotato.class)
public interface RenderTileTinyPotatoAccessor {
    @Invoker("getRenderLayer") static RenderLayer getRenderLayer(ShaderHelper.BotaniaShader shader, String name){
        throw new AssertionError();
    }

    @Invoker("stripShaderName") static Pair<ShaderHelper.BotaniaShader, String> stripShaderName(String name){
        throw new AssertionError();
    }
}
