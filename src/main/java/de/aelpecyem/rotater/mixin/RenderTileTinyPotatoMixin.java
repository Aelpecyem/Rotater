package de.aelpecyem.rotater.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.item.TinyPotatoRenderCallback;
import vazkii.botania.client.core.helper.ShaderHelper;
import vazkii.botania.client.render.tile.RenderTileTinyPotato;
import vazkii.botania.common.block.tile.TileTinyPotato;

@Mixin(RenderTileTinyPotato.class)
public abstract class RenderTileTinyPotatoMixin {
    @Shadow @Final private ModelPart potatoModel;

    @Shadow protected abstract void renderItems(TileTinyPotato potato, Direction facing, String name, float partialTicks, MatrixStack ms, VertexConsumerProvider buffers, int light, int overlay);

    @Shadow protected abstract void renderName(TileTinyPotato potato, String name, MatrixStack ms, VertexConsumerProvider buffers, int light);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = false)
    private void renderRotater(TileTinyPotato potato, float partialTicks, MatrixStack ms, VertexConsumerProvider buffers, int light, int overlay, CallbackInfo ci){
        String name = potato.name.getString().toLowerCase().trim();
        if (name.equals("rotater")) {
            ms.push();
            Pair<ShaderHelper.BotaniaShader, String> shaderStrippedName = RenderTileTinyPotatoAccessor.stripShaderName(name);
            ShaderHelper.BotaniaShader shader = shaderStrippedName.getFirst();
            name = shaderStrippedName.getSecond();
            RenderLayer layer = RenderTileTinyPotatoAccessor.getRenderLayer(shader, name);

            ms.translate(0.5F, 1.5F, 0.5F);
            ms.scale(1F, -1F, -1F);

            Direction potatoFacing = potato.getCachedState().get(Properties.HORIZONTAL_FACING);
            float rotY = 0;
            switch (potatoFacing) {
                default:
                case SOUTH:
                    break;
                case NORTH:
                    rotY = 180F;
                    break;
                case EAST:
                    rotY = 270F;
                    break;
                case WEST:
                    rotY = 90F;
                    break;
            }
            float jump = potato.jumpTicks;
            if (jump > 0) {
                jump -= partialTicks;
            }
            rotY += (float) Math.sin(jump / 40 * Math.PI) * 360;
            ms.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotY));
            ms.push();
            VertexConsumer buffer = buffers.getBuffer(layer);
            potatoModel.render(ms, buffer, light, overlay, 1, 1, 1, 1);
            ms.pop();
            renderItems(potato, potatoFacing, name, partialTicks, ms, buffers, light, overlay);
            ms.push();
            TinyPotatoRenderCallback.EVENT.invoker().onRender(potato, potato.name, partialTicks, ms, buffers, light, overlay);
            ms.pop();
            ms.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-rotY));
            ms.scale(1F, -1F, -1F);
            renderName(potato, name, ms, buffers, light);
            ms.pop();
            ci.cancel();
        }
    }
}
