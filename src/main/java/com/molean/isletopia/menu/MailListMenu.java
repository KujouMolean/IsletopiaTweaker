package com.molean.isletopia.menu;

import com.molean.isletopia.shared.database.MailboxDao;
import com.molean.isletopia.shared.model.Mail;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.BukkitPlayerUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

import java.sql.SQLException;
import java.util.List;

public class MailListMenu extends ListMenu<Mail> {

    public MailListMenu(Player player) {
        super(player, Component.text("邮件列表"));
        try {
            this.components(MailboxDao.getMails(player.getUniqueId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.convertFunction(mail -> {
            ItemStack itemStack = ItemStack.deserializeBytes(mail.getData());
            return ItemStackSheet.fromString(itemStack, mail.toString()).build();
        });
        this.onClickAsync(mail -> {
            if (mail.isClaimed()) {
                return;
            }
            try {
                mail.setClaimed(true);
                MailboxDao.claim(mail.getId());
                ItemStack itemStack = ItemStack.deserializeBytes(mail.getData());
                BundleMeta bundleMeta = (BundleMeta) itemStack.getItemMeta();
                List<ItemStack> items = bundleMeta.getItems();
                BukkitPlayerUtils.giveItem(player, items.toArray(new ItemStack[0]));
                close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}
