package me.badbones69.crazycrates.api.objects;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.controllers.FileManager;
import me.badbones69.crazycrates.controllers.Preview;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Crate {
	private String name;
	private ItemStack key;
	private Integer maxPage = 1;
	private String previewName;
	private CrateType crateType;
	private FileConfiguration file;
	private ArrayList<Prize> prizes;
	private String crateInventoryName;
	private Boolean giveNewPlayerKeys;
	private Integer newPlayerKeys;
	private ArrayList<ItemStack> preview;
	private FileManager fileManager = FileManager.getInstance();
	
	/**
	 *
	 * @param name The name of the crate.
	 * @param crateType The crate type of the crate.
	 * @param key The key as an item stack.
	 * @param prizes The prizes that can be won.
	 * @param file The crate file.
	 */
	public Crate(String name, String previewName, CrateType crateType, ItemStack key, ArrayList<Prize> prizes, FileConfiguration file, Integer newPlayerKeys) {
		this.key = key;
		this.file = file;
		this.name = name;
		this.prizes = prizes;
		this.crateType = crateType;
		this.preview = loadPreview();
		this.previewName = Methods.color(previewName);
		this.newPlayerKeys = newPlayerKeys;
		this.giveNewPlayerKeys = newPlayerKeys > 0;
		for(int amount = preview.size(); amount > 36; amount -= 45, maxPage++) ;
		this.crateInventoryName = file != null ? Methods.color(file.getString("Crate.CrateName")) : "";
	}
	
	/**
	 * Picks a random prize based on BlackList Permissions and the Chance System.
	 * @param player The player that will be winning the prize.
	 * @return The winning prize.
	 */
	public Prize pickPrize(Player player) {
		ArrayList<Prize> prizes = new ArrayList<>();
		ArrayList<Prize> useablePrizes = new ArrayList<>();
		// ================= Blacklist Check ================= //
		if(player.isOp()) {
			useablePrizes.addAll(getPrizes());
		}else {
			for(Prize prize : getPrizes()) {
				if(prize.hasBlacklistPermission(player)) {
					if(!prize.hasAltPrize()) {
						continue;
					}
				}
				useablePrizes.add(prize);
			}
		}
		// ================= Chance Check ================= //
		for(int stop = 0; prizes.size() == 0 && stop <= 2000; stop++) {
			for(Prize prize : useablePrizes) {
				int max = prize.getMaxRange();
				int chance = prize.getChance();
				int num;
				for(int counter = 1; counter <= 1; counter++) {
					num = 1 + new Random().nextInt(max);
					if(num >= 1 && num <= chance) {
						prizes.add(prize);
					}
				}
			}
		}
		return prizes.get(new Random().nextInt(prizes.size()));
	}
	
	/**
	 * Picks a random prize based on BlackList Permissions and the Chance System. Only used in the Cosmic Crate Type since it is the only one with tiers.
	 * @param player The player that will be winning the prize.
	 * @param tier The tier you wish the prize to be from.
	 * @return The winning prize based on the crate's tiers.
	 */
	public Prize pickPrize(Player player, String tier) {
		ArrayList<Prize> prizes = new ArrayList<>();
		ArrayList<Prize> useablePrizes = new ArrayList<>();
		// ================= Blacklist Check ================= //
		if(player.isOp()) {
			for(Prize prize : getPrizes()) {
				if(prize.getTiers().contains(tier.toLowerCase())) {
					useablePrizes.add(prize);
				}
			}
		}else {
			for(Prize prize : getPrizes()) {
				if(prize.hasBlacklistPermission(player)) {
					if(!prize.hasAltPrize()) {
						continue;
					}
				}
				if(prize.getTiers().contains(tier.toLowerCase())) {
					useablePrizes.add(prize);
				}
			}
		}
		// ================= Chance Check ================= //
		for(int stop = 0; prizes.size() == 0 && stop <= 2000; stop++) {
			for(Prize prize : useablePrizes) {
				int max = prize.getMaxRange();
				int chance = prize.getChance();
				int num;
				for(int counter = 1; counter <= 1; counter++) {
					num = 1 + new Random().nextInt(max);
					if(num >= 1 && num <= chance) {
						prizes.add(prize);
					}
				}
			}
		}
		return prizes.get(new Random().nextInt(prizes.size()));
	}
	
	/**
	 * Picks a random prize based on BlackList Permissions and the Chance System. Spawns the display item at the location.
	 * @param player The player that will be winning the prize.
	 * @param location The location the prize will spawn at.
	 * @return The winning prize.
	 */
	public Prize pickPrize(Player player, Location location) {
		ArrayList<Prize> prizes = new ArrayList<>();
		ArrayList<Prize> useablePrizes = new ArrayList<>();
		// ================= Blacklist Check ================= //
		if(player.isOp()) {
			useablePrizes.addAll(getPrizes());
		}else {
			for(Prize prize : getPrizes()) {
				if(prize.hasBlacklistPermission(player)) {
					if(!prize.hasAltPrize()) {
						continue;
					}
				}
				useablePrizes.add(prize);
			}
		}
		// ================= Chance Check ================= //
		for(int stop = 0; prizes.size() == 0 && stop <= 2000; stop++) {
			for(Prize prize : useablePrizes) {
				int max = prize.getMaxRange();
				int chance = prize.getChance();
				int num;
				for(int counter = 1; counter <= 1; counter++) {
					num = 1 + new Random().nextInt(max);
					if(num >= 1 && num <= chance) {
						prizes.add(prize);
					}
				}
			}
		}
		Prize prize = prizes.get(new Random().nextInt(prizes.size()));
		if(prize.useFireworks()) {
			Methods.fireWork(location);
		}
		return prize;
	}
	
	/**
	 *
	 * @return name The name of the crate.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 *
	 * @return The name of the crate's preview page.
	 */
	public String getPreviewName() {
		return previewName;
	}
	
	/**
	 * Get the name of the inventory the crate will have.
	 * @return The name of the inventory for GUI based crate types.
	 */
	public String getCrateInventoryName() {
		return this.crateInventoryName;
	}
	
	/**
	 * Gets the inventory of a preview of prizes for the crate.
	 * @return The preview as an Inventory object.
	 */
	public Inventory getPreview(Player player) {
		Inventory inv = Bukkit.createInventory(null, 54, previewName);
		setDefaultItems(inv, player);
		for(ItemStack item : getPageItems(Preview.getPage(player))) {
			int nextSlot = inv.firstEmpty();
			if(nextSlot >= 0) {
				inv.setItem(nextSlot, item);
			}else {
				break;
			}
		}
		return inv;
	}
	
	/**
	 * Gets the inventory of a preview of prizes for the crate.
	 * @return The preview as an Inventory object.
	 */
	public Inventory getPreview(Player player, int page) {
		Inventory inv = Bukkit.createInventory(null, 54, previewName);
		setDefaultItems(inv, player);
		for(ItemStack item : getPageItems(page)) {
			int nextSlot = inv.firstEmpty();
			if(nextSlot >= 0) {
				inv.setItem(nextSlot, item);
			}else {
				break;
			}
		}
		return inv;
	}
	
	/**
	 * Gets all the preview items.
	 * @return A list of all the preview items.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ItemStack> getPreviewItems() {
		return (ArrayList<ItemStack>) preview.clone();
	}
	
	/**
	 *
	 * @return The crate type of the crate.
	 */
	public CrateType getCrateType() {
		return this.crateType;
	}
	
	/**
	 *
	 * @return The key as an item stack.
	 */
	public ItemStack getKey() {
		return this.key.clone();
	}
	
	/**
	 *
	 * @param amount The amount of keys you want.
	 * @return The key as an item stack.
	 */
	public ItemStack getKey(int amount) {
		ItemStack key = this.key.clone();
		key.setAmount(amount);
		return key;
	}
	
	/**
	 *
	 * @return The crates file.
	 */
	public FileConfiguration getFile() {
		return this.file;
	}
	
	/**
	 *
	 * @return The prizes in the crate.
	 */
	public ArrayList<Prize> getPrizes() {
		return this.prizes;
	}
	
	/**
	 *
	 * @param name Name of the prize you want.
	 * @return The prize you asked for.
	 */
	public Prize getPrize(String name) {
		for(Prize prize : prizes) {
			if(prize.getName().equalsIgnoreCase(name)) {
				return prize;
			}
		}
		return null;
	}
	
	/**
	 *
	 * @return True if new players get keys and false if they do not.
	 */
	public Boolean doNewPlayersGetKeys() {
		return giveNewPlayerKeys;
	}
	
	/**
	 *
	 * @return The number of keys new players get.
	 */
	public Integer getNewPlayerKeys() {
		return newPlayerKeys;
	}
	
	public void addEditorItem(String prize, ItemStack item) {
		ArrayList<ItemStack> items = new ArrayList<>();
		items.add(item);
		String path = "Crate.Prizes." + prize;
		if(file.contains(path + ".Editor-Items")) {
			for(Object list : file.getList(path + ".Editor-Items")) {
				items.add((ItemStack) list);
			}
		}
		
		String displayName = item.getItemMeta().getDisplayName();
		if(displayName == null) displayName = "&6"+prize;
		List<String> lore = item.getItemMeta().getLore();
		if(lore == null) lore = new ArrayList<>();
		List<String> enchantments = new ArrayList<>();
		item.getEnchantments().forEach((ench, level)->{
			enchantments.add(ench.getName()+":"+level);
		});
		
		if(!file.contains(path + ".DisplayName")) file.set(path + ".DisplayName", displayName);
		if(!file.contains(path + ".DisplayItem")) file.set(path + ".DisplayItem", item.getType().name()+":"+item.getData().getData());
		if(!file.contains(path + ".DisplayAmount")) file.set(path + ".DisplayAmount", item.getAmount());
		if(!file.contains(path + ".Lore")) file.set(path + ".Lore", lore);
		if(!file.contains(path + ".DisplayEnchantments")) file.set(path + ".DisplayEnchantments", enchantments);
		if(!file.contains(path + ".MaxRange")) file.set(path + ".MaxRange", 100);
		if(!file.contains(path + ".Chance")) file.set(path + ".Chance", 50);
		if(!file.contains(path + ".Firework")) file.set(path + ".Firework", false);
		if(!file.contains(path + ".Glowing")) file.set(path + ".Glowing", false);
		if(!file.contains(path + ".Player")) file.set(path + ".Player", "");
		if(!file.contains(path + ".Unbreakable")) file.set(path + ".Unbreakable", false);
		if(!file.contains(path + ".Items")) file.set(path + ".Items", new ArrayList<>());
		file.set(path + ".Editor-Items", items);
		if(!file.contains(path + ".Commands")) file.set(path + ".Commands", new ArrayList<>());
		if(!file.contains(path + ".Messages")) file.set(path + ".Messages", new ArrayList<>());
		if(!file.contains(path + ".BlackListed-Permissions")) file.set(path + ".BlackListed-Permissions", new ArrayList<>());
		if(!file.contains(path + ".Alternative-Prize.Toggle")) file.set(path + ".Alternative-Prize.Toggle", false);
		if(!file.contains(path + ".Alternative-Prize.Commands")) file.set(path + ".Alternative-Prize.Commands", new ArrayList<>());
		if(!file.contains(path + ".Alternative-Prize.Items")) file.set(path + ".Alternative-Prize.Items", new ArrayList<>());
		if(!file.contains(path + ".Alternative-Prize.Messages")) file.set(path + ".Alternative-Prize.Messages", new ArrayList<>());
		fileManager.saveFile(fileManager.getFile(name));
	}
	
	/**
	 *
	 * @return The max page for the preview.
	 */
	public int getMaxPage() {
		return maxPage;
	}
	
	/**
	 * Loads all the preview items and puts them into a list.
	 * @return A list of all the preview items that were created.
	 */
	private ArrayList<ItemStack> loadPreview() {
		ArrayList<ItemStack> items = new ArrayList<>();
		for(Prize prize : getPrizes()) {
			items.add(prize.getDisplayItem());
		}
		return items;
	}
	
	private List<ItemStack> getPageItems(Integer page) {
		List<ItemStack> list = preview;
		List<ItemStack> items = new ArrayList<>();
		if(page <= 0) page = 1;
		int max = 36;
		int index = page * max - max;
		int endIndex = index >= list.size() ? list.size() - 1 : index + max;
		for(; index < endIndex; index++) {
			if(index < list.size()) items.add(list.get(index));
		}
		for(; items.size() == 0; page--) {
			if(page <= 0) break;
			index = page * max - max;
			endIndex = index >= list.size() ? list.size() - 1 : index + max;
			for(; index < endIndex; index++) {
				if(index < list.size()) items.add(list.get(index));
			}
		}
		return items;
	}
	
	private void setDefaultItems(Inventory inv, Player player) {
		for(int i : Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 45, 46, 47, 49, 51, 52, 53)) {
			inv.setItem(i, new ItemBuilder()
			.setMaterial(Material.STAINED_GLASS_PANE)
			.setMetaData((short) Preview.getPaneColor(player))
			.setName(" ")
			.build());
		}
		int page = Preview.getPage(player);
		int maxPage = getMaxPage();
		if(Preview.playerInMenu(player)) {
			inv.setItem(49, Preview.getMenuButton());
		}
		if(page == 1) {
			inv.setItem(48, new ItemBuilder()
			.setMaterial(Material.STAINED_GLASS_PANE)
			.setMetaData((short) 7)
			.setName(" ")
			.build());
		}else {
			inv.setItem(48, Preview.getBackButton(player));
		}
		if(page == maxPage) {
			inv.setItem(50, new ItemBuilder()
			.setMaterial(Material.STAINED_GLASS_PANE)
			.setMetaData((short) 7)
			.setName(" ")
			.build());
		}else {
			inv.setItem(50, Preview.getNextButton(player));
		}
	}
}