package com.theyugin.nee.service.thaumcraft;

import com.google.inject.Inject;
import com.theyugin.nee.config.ExportConfigOption;
import com.theyugin.nee.data.thaumcraft.Aspect;
import com.theyugin.nee.render.StackRenderer;
import com.theyugin.nee.service.AbstractCacheableService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import lombok.SneakyThrows;
import lombok.val;

public class AspectService extends AbstractCacheableService<Aspect> {
    private final PreparedStatement aspectInsertStatement;

    @Inject
    @SneakyThrows
    public AspectService(Connection conn) {
        this.aspectInsertStatement =
                conn.prepareStatement("insert or ignore into aspect (tag, name, icon) values (?, ?, ?)");
    }

    @SneakyThrows
    public Aspect processAspect(thaumcraft.api.aspects.Aspect aspect) {
        val tag = aspect.getTag();
        val name = aspect.getName();
        val resultAspect = Aspect.builder().tag(tag).name(name).build();
        if (putInCache(resultAspect)) {
            return resultAspect;
        }
        byte[] icon;
        if (ExportConfigOption.ICONS.get()) {
            icon = StackRenderer.renderIcon(aspect);
        } else {
            icon = null;
        }
        aspectInsertStatement.setString(1, tag);
        aspectInsertStatement.setString(2, name);
        aspectInsertStatement.setBytes(3, icon);
        aspectInsertStatement.executeUpdate();
        return resultAspect;
    }
}
