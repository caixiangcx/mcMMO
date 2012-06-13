package com.gmail.nossr50.skills.archery;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;


public class ArcheryManager {
    private Player player;
    private PlayerProfile profile;
    private int skillLevel;
    private Permissions permissionsInstance;

    public ArcheryManager (Player player) {
        this.player = player;
        this.profile = Users.getProfile(player);
        this.skillLevel = profile.getSkillLevel(SkillType.TAMING);
        this.permissionsInstance =  Permissions.getInstance();
    }

    /**
     * Track arrows fired for later retrieval.
     *
     * @param livingEntity Entity damaged by the arrow
     */
    public void trackArrows(LivingEntity livingEntity) {
        if (!permissionsInstance.trackArrows(player)) {
            return;
        }

        ArrowTrackingEventHandler eventHandler = new ArrowTrackingEventHandler(this, livingEntity);

        if (Archery.getRandom().nextInt(1000) <= eventHandler.skillModifier) {
            eventHandler.addToTracker();
        }
    }

    /**
     * Check for Daze.
     *
     * @param defender Defending player
     * @param event The event to modify
     */
    public void dazeCheck(Player defender, EntityDamageEvent event) {
        if (!permissionsInstance.daze(player)) {
            return;
        }

        DazeEventHandler eventHandler = new DazeEventHandler(this, event, defender);

        if (Archery.getRandom().nextInt(2000) <= eventHandler.skillModifier) {
            eventHandler.handleDazeEffect();
            eventHandler.sendAbilityMessages();
        }
    }

    /**
     * Handle archery bonus damage.
     *
     * @param event The event to modify.
     */
    public void bonusDamage(EntityDamageEvent event) {
        if (!permissionsInstance.archeryBonus(player)) {
            return;
        }

        if (skillLevel >= Archery.BONUS_DAMAGE_INCREASE_LEVEL) {
            BonusDamageEventHandler eventHandler = new BonusDamageEventHandler(this, event);

            eventHandler.calculateDamageBonus();
            eventHandler.modifyEventDamage();
        }
    }

    protected int getSkillLevel() {
        return skillLevel;
    }

    protected Player getPlayer() {
        return player;
    }
}
