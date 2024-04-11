package com.tmtravlr.soundfilters;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import paulscode.sound.Source;

/**
 * This class handles the tick events and updates the filter parameters for each
 * source playing.
 *
 * @author Rebeca Rey
 * @Date 2014
 */
public class SoundTickHandler {
	private Minecraft mc = Minecraft.getMinecraft();
	private static int profileTickCountdown = 13;
	private static float prevDecayFactor = 0.0F;
	private static float prevRoomFactor = 0.0F;
	private static float prevSkyFactor = 0.0F;
	public static float targetLowPassGain = 1.0F;
	public static float targetLowPassGainHF = 1.0F;
	public static float baseLowPassGain = 1.0F;
	public static float baseLowPassGainHF = 1.0F;
	public static boolean waterSound = false;
	public static boolean lavaSound = false;

	public static ConcurrentSkipListMap<ComparablePosition, DoubleWithTimeout> sourceOcclusionMap = new ConcurrentSkipListMap<>();

	/**
	 * Represents a x, y, z position which has a comparator and a few methods
	 * that make comparing it with another ComparablePositions very fast. Used
	 * for fast-access sets/maps.
	 *
	 * @author Rebeca Rey
	 * @Date 2014
	 */
	public static class ComparablePosition implements Comparable<ComparablePosition> {
		float x;
		float y;
		float z;

		ComparablePosition(float xToSet, float yToSet, float zToSet) {
			this.x = xToSet;
			this.y = yToSet;
			this.z = zToSet;
		}

		// Return the int floor of x
		public int x() {
			return MathHelper.floor(x);
		}

		// Return the int floor of y
		public int y() {
			return MathHelper.floor(y);
		}

		// Return the int floor of z
		public int z() {
			return MathHelper.floor(z);
		}

		@Override
		public boolean equals(Object object) {
			if (!(object instanceof ComparablePosition)) {
				return false;
			}
			ComparablePosition toCompare = (ComparablePosition) object;

			return toCompare.compareTo(this) == 0;
		}

		@Override
		public int compareTo(ComparablePosition toCompare) {
			if (toCompare.x - this.x > 0.1) {
				return 1;
			} else if (toCompare.x - this.x < -0.1) {
				return -1;
			} else if (toCompare.y - this.y > 0.1) {
				return 1;
			} else if (toCompare.y - this.y < -0.1) {
				return -1;
			} else if (toCompare.z - this.z > 0.1) {
				return 1;
			} else if (toCompare.z - this.z < -0.1) {
				return -1;
			} else {
				return 0;
			}
		}

		@Override
		public String toString() {
			return "[" + x + ", " + y + ", " + z + "]";
		}
	}

	/**
	 * Represents two values for use in the occlusion: the sound source and
	 * occlusion amount. Also has a timeout variables, which the source should
	 * be constantly setting to 10. When the source stops setting it to 10, the
	 * sound is assumed to have finished playing.
	 *
	 * @author Rebeca Rey
	 * @Date 2014
	 */
	public static class DoubleWithTimeout {
		public Source source;
		public double amount;
		public int timeout;

		DoubleWithTimeout() {
			this(null, 0.0D, 10);
		}

		DoubleWithTimeout(Source sToSet, double dToSet, int iToSet) {
			this.source = sToSet;
			this.amount = dToSet;
			this.timeout = iToSet;
		}
	}

	@SubscribeEvent
	public void tick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			if (this.mc != null && this.mc.world != null && this.mc.player != null && !this.mc.isGamePaused()) {
				// Handle the low-pass inside of liquids
				if (this.mc.player.isInsideOfMaterial(Material.WATER)) {
					if (!waterSound) {
						if (SoundFiltersConfig.DEBUG) {
							SoundFiltersConfig.LOGGER.info("[SoundFilters] Applying water sound low pass.");
						}

						targetLowPassGain = SoundFiltersConfig.WATER_VOLUME;
						targetLowPassGainHF = SoundFiltersConfig.WATER_LOW_PASS_AMOUNT;
						lavaSound = false;
						waterSound = true;
					}
				} else if (waterSound) {
					if (SoundFiltersConfig.DEBUG) {
						SoundFiltersConfig.LOGGER.info("[SoundFilters] Stopping water sound low pass.");
					}

					targetLowPassGain = 1.0F;
					targetLowPassGainHF = 1.0F;
					waterSound = false;
				}

				if (this.mc.player.isInsideOfMaterial(Material.LAVA)) {
					if (!lavaSound) {
						if (SoundFiltersConfig.DEBUG) {
							SoundFiltersConfig.LOGGER.info("[SoundFilters] Applying lava sound low pass.");
						}

						targetLowPassGain = SoundFiltersConfig.LAVA_VOLUME;
						targetLowPassGainHF = SoundFiltersConfig.LAVA_LOW_PASS_AMOUNT;
						lavaSound = true;
						waterSound = false;
					}
				} else if (lavaSound) {
					if (SoundFiltersConfig.DEBUG) {
						SoundFiltersConfig.LOGGER.info("[SoundFilters] Stopping lava sound low pass.");
					}

					targetLowPassGain = 1.0F;
					targetLowPassGainHF = 1.0F;
					lavaSound = false;
				}

				if (Math.abs(targetLowPassGain - baseLowPassGain) > 0.001F) {
					baseLowPassGain = (targetLowPassGain + baseLowPassGain) / 2;
				}

				if (Math.abs(targetLowPassGainHF - baseLowPassGainHF) > 0.001F) {
					baseLowPassGainHF = (targetLowPassGainHF + baseLowPassGainHF) / 2;
				}
			} else {
				// We must be in the menu; turn everything off:

				baseLowPassGain = 1.0F;
				baseLowPassGainHF = 1.0F;
				SoundFiltersConfig.REVERB_FILTER.decayTime = 0.1F;
				SoundFiltersConfig.REVERB_FILTER.reflectionsDelay = 0.0F;
				SoundFiltersConfig.REVERB_FILTER.lateReverbDelay = 0.0F;
				lavaSound = false;
				waterSound = false;
			}
		} else {
			ArrayList<ComparablePosition> toRemove = new ArrayList<ComparablePosition>();

			// Calculate sound occlusion for all the sources playing.
			if (SoundFiltersConfig.DO_OCCLUSION) {
				for (ComparablePosition sourcePosition : sourceOcclusionMap.keySet()) {
					DoubleWithTimeout sourceAndAmount = (DoubleWithTimeout) sourceOcclusionMap.get(sourcePosition);
					if (sourceAndAmount != null) {
						if (sourceAndAmount.source != null && sourceAndAmount.source.position != null && sourceAndAmount.source.active()) {

							// The source can sometimes be modified in another thread, causing a rare null pointer exception here. It seems to happen when a lot of mods are installed.
							try {
								if (this.mc != null && this.mc.world != null && this.mc.player != null) {
									Vec3d roomSize = new Vec3d(this.mc.player.posX, this.mc.player.posY + (double) this.mc.player.getEyeHeight(), this.mc.player.posZ);
										sourceAndAmount.amount = (sourceAndAmount.amount * 3 + SoundFiltersConfig.OCCLUSION_PERCENT * getSoundOcclusion(this.mc.world, new Vec3d((double) sourceAndAmount.source.position.x, (double) sourceAndAmount.source.position.y, (double) sourceAndAmount.source.position.z), roomSize)) / 4.0;
								} else {
									sourceAndAmount.amount = 0.0D;
								}
							} catch (NullPointerException e) {
								if (SoundFiltersConfig.DEBUG) {
									SoundFiltersConfig.LOGGER.warn("Caught null pointer exception while updating sound occlusion. This happens sometimes because a sound is modified at the same time in another thread.");
								}
							}
						} else {
							toRemove.add(sourcePosition);
						}
					}
				}

				for (ComparablePosition positionToRemove : toRemove) {
					if (SoundFiltersConfig.SUPER_DUPER_DEBUG)
						SoundFiltersConfig.LOGGER.info("[Sound Filters] Removing " + positionToRemove + ", " + positionToRemove.hashCode() + ", " + profileTickCountdown);
					sourceOcclusionMap.remove(positionToRemove);
				}
			}

			// Create a profile of the reverb in the area.
			if (this.mc != null && this.mc.world != null && this.mc.player != null && SoundFiltersConfig.DO_REVERB) {
				// Only run every 13 ticks.
				if (--profileTickCountdown <= 0) {
					profileTickCountdown = 13;
					
					Random rand = new Random();
					TreeSet<ComparablePosition> visited = new TreeSet<ComparablePosition>();
					ArrayList<IBlockState> blocksFound = new ArrayList<IBlockState>();

					LinkedList<ComparablePosition> toVisit = new LinkedList<ComparablePosition>();
					toVisit.add(new ComparablePosition(MathHelper.floor(this.mc.player.posX), MathHelper.floor(this.mc.player.posY), MathHelper.floor(this.mc.player.posZ)));
					IBlockState state;

					// Flood fill through the area the player is in, with
					// maximum size of
					// SoundFiltersConfig.profileSize (the size in the config file)
					int i;
					for (i = 0; i < SoundFiltersConfig.PROFILE_SIZE && !toVisit.isEmpty(); i++) {
						ComparablePosition current = (ComparablePosition) toVisit.remove(rand.nextInt(toVisit.size()));
						visited.add(current);

						// Check for valid neighbours in 6 directions (valid
						// means a non-solid block that hasn't been visited)

						// South
						ComparablePosition pos = new ComparablePosition(current.x, current.y, current.z + 1);
						state = this.mc.world.getBlockState(new BlockPos(pos.x(), pos.y(), pos.z()));
						Material material = state.getMaterial();

						if (!material.blocksMovement()) {
							if (!visited.contains(pos) && !toVisit.contains(pos)) {
								toVisit.add(pos);
							}

							if (material != Material.AIR) {
								blocksFound.add(state);
							}
						} else {
							blocksFound.add(state);
						}

						// North
						pos = new ComparablePosition(current.x, current.y, current.z - 1);
						state = this.mc.world.getBlockState(new BlockPos(pos.x(), pos.y(), pos.z()));
						material = state.getMaterial();

						if (!material.blocksMovement()) {
							if (!visited.contains(pos) && !toVisit.contains(pos)) {
								toVisit.add(pos);
							}

							if (material != Material.AIR) {
								blocksFound.add(state);
							}
						} else {
							blocksFound.add(state);
						}

						// Up
						pos = new ComparablePosition(current.x, current.y + 1, current.z);
						state = this.mc.world.getBlockState(new BlockPos(pos.x(), pos.y(), pos.z()));
						material = state.getMaterial();

						if (!material.blocksMovement()) {
							if (!visited.contains(pos) && !toVisit.contains(pos)) {
								toVisit.add(pos);
							}

							if (material != Material.AIR) {
								blocksFound.add(state);
							}
						} else {
							blocksFound.add(state);
						}

						// Down
						pos = new ComparablePosition(current.x, current.y - 1, current.z);
						state = this.mc.world.getBlockState(new BlockPos(pos.x(), pos.y(), pos.z()));
						material = state.getMaterial();

						if (!material.blocksMovement()) {
							if (!visited.contains(pos) && !toVisit.contains(pos)) {
								toVisit.add(pos);
							}

							if (material != Material.AIR) {
								blocksFound.add(state);
							}
						} else {
							blocksFound.add(state);
						}

						// East
						pos = new ComparablePosition(current.x + 1, current.y, current.z);
						state = this.mc.world.getBlockState(new BlockPos(pos.x(), pos.y(), pos.z()));
						material = state.getMaterial();

						if (!material.blocksMovement()) {
							if (!visited.contains(pos) && !toVisit.contains(pos)) {
								toVisit.add(pos);
							}

							if (material != Material.AIR) {
								blocksFound.add(state);
							}
						} else {
							blocksFound.add(state);
						}

						// West
						pos = new ComparablePosition(current.x - 1, current.y, current.z);
						state = this.mc.world.getBlockState(new BlockPos(pos.x(), pos.y(), pos.z()));
						material = state.getMaterial();

						if (!material.blocksMovement()) {
							if (!visited.contains(pos) && !toVisit.contains(pos)) {
								toVisit.add(pos);
							}

							if (material != Material.AIR) {
								blocksFound.add(state);
							}
						} else {
							blocksFound.add(state);
						}
					}

					// Now we've gone through the whole room, or we hit the size
					// limit.
					// Figure out the reverb from the blocks found.

					int roomSize = visited.size();
					double highReverb = 0;
					double midReverb = 0;
					double lowReverb = 0;

					for (IBlockState s : blocksFound) {
						Block b = s.getBlock();

						// Check for custom reverb blocks
						Double value = getCustomValue(b, b.getMetaFromState(s), SoundFiltersConfig.CUSTOM_OCCLUSION);

						if (value != null) {
							double factor = value.doubleValue();

							lowReverb += factor >= 1.0 || factor < 0.0 ? 0.0 : 1.0 - factor;
							midReverb += factor >= 2.0 || factor <= 0.0 ? 0.0 : 1.0 - Math.abs(factor - 1.0);
							highReverb += factor <= 1.0 ? 0.0 : factor > 2.0 ? 1.0 : factor - 1.0;
						} else if (s.getMaterial() != Material.ROCK && s.getMaterial() != Material.GLASS && s.getMaterial() != Material.ICE && s.getMaterial() != Material.IRON) {
							if (s.getMaterial() != Material.CACTUS && s.getMaterial() != Material.CAKE && s.getMaterial() != Material.CLOTH && s.getMaterial() != Material.CORAL
									&& s.getMaterial() != Material.GRASS && s.getMaterial() != Material.LEAVES && s.getMaterial() != Material.CARPET && s.getMaterial() != Material.PLANTS
									&& s.getMaterial() != Material.GOURD && s.getMaterial() != Material.SNOW && s.getMaterial() != Material.SPONGE && s.getMaterial() != Material.VINE
									&& s.getMaterial() != Material.WEB) {
								// Generic materials that don't fall into either
								// catagory (wood, dirt, etc.)
								++midReverb;
							} else {
								// Reverb-absorbing materials (like wool and
								// plants)
								++lowReverb;
							}
						} else {
							// Materials that relfect sound (smooth and solid
							// like stone, glass, and ice)
							++highReverb;
						}

					}

					float skyFactor = 0.0F;

					if (SoundFiltersConfig.DO_SKY_CHECKS && roomSize == SoundFiltersConfig.PROFILE_SIZE) {
						/*
						 * Check if you and blocks around you can see the sky in
						 * a pattern like so:
						 *
						 * B B B
						 *
						 * B P B
						 *
						 * B B B
						 *
						 * With distances of random from 5 to 10, and also above
						 * 5 for the B's
						 */

						int x = MathHelper.floor(mc.player.posX);
						int y = MathHelper.floor(mc.player.posY);
						int z = MathHelper.floor(mc.player.posZ);

						skyFactor += onlySkyAboveBlock(mc.world, x, y, z);
						skyFactor += onlySkyAboveBlock(mc.world, x + rand.nextInt(8) - 4, y, z);
						skyFactor += onlySkyAboveBlock(mc.world, x + rand.nextInt(8) - 4, y, z);
						skyFactor += onlySkyAboveBlock(mc.world, x, y, z + rand.nextInt(8) - 4);
						skyFactor += onlySkyAboveBlock(mc.world, x, y, z + rand.nextInt(8) - 4);
						skyFactor += onlySkyAboveBlock(mc.world, x + rand.nextInt(8) - 4, y, z + rand.nextInt(8) - 4);
						skyFactor += onlySkyAboveBlock(mc.world, x + rand.nextInt(8) - 4, y, z + rand.nextInt(8) - 4);
						skyFactor += onlySkyAboveBlock(mc.world, x + rand.nextInt(8) - 4, y, z + rand.nextInt(8) - 4);
						skyFactor += onlySkyAboveBlock(mc.world, x + rand.nextInt(8) - 4, y, z + rand.nextInt(8) - 4);
					}

					skyFactor = 1.0F - Math.min(skyFactor, 12F) / 12F;

					float decayFactor = 0.0F;
					float roomFactor = (float) roomSize / (float) SoundFiltersConfig.PROFILE_SIZE;

					if (highReverb + midReverb + lowReverb > 0) {
						decayFactor += (float) (highReverb - lowReverb) / (float) (highReverb + midReverb + lowReverb);
					}

					if (decayFactor < 0.0F) {
						decayFactor = 0.0F;
					}

					if (decayFactor > 1.0F) {
						decayFactor = 1.0F;
					}

					if (SoundFiltersConfig.SUPER_DUPER_DEBUG) {
						SoundFiltersConfig.LOGGER.info("[Sound Filters] Reverb Profile - Room Size: " + roomSize + ", Looked at: " + i + ", Sky Factor: " + skyFactor + ", High, Mid, and Low Reverb: ("
								+ highReverb + ", " + midReverb + ", " + lowReverb + ")");
					}

					decayFactor = (decayFactor + prevDecayFactor) / 2.0F;
					roomFactor = (roomFactor + prevRoomFactor) / 2.0F;
					skyFactor = (skyFactor + prevSkyFactor) / 2.0F;

					prevDecayFactor = decayFactor;
					prevRoomFactor = roomFactor;
					prevSkyFactor = skyFactor;

					Float baseValue = SoundFiltersConfig.DIM_REVERB.get(this.mc.player.world.provider.getDimensionType().getName());
					float base = baseValue != null ? SoundFiltersConfig.REVERB_PERCENT * baseValue.floatValue() : SoundFiltersConfig.REVERB_PERCENT ;

					SoundFiltersConfig.REVERB_FILTER.decayTime = base * 8.0F * decayFactor * roomFactor * skyFactor;

					if (SoundFiltersConfig.REVERB_FILTER.decayTime < 0.1F) {
						SoundFiltersConfig.REVERB_FILTER.decayTime = 0.1F;
					}

					SoundFiltersConfig.REVERB_FILTER.reflectionsGain = base * (0.05F + (0.05F * roomFactor));
					SoundFiltersConfig.REVERB_FILTER.reflectionsDelay = 0.025F * roomFactor;
					SoundFiltersConfig.REVERB_FILTER.lateReverbGain = base * (1.26F + (0.1F * roomFactor));
					SoundFiltersConfig.REVERB_FILTER.lateReverbDelay = 0.01F * roomFactor;
				}
			}
		}
	}

	private static float onlySkyAboveBlock(World world, int x, int y, int z) {
		int result = 20;
		for (int i = y; i < 256; i++) {
			IBlockState state = world.getBlockState(new BlockPos(x, i, z));

			if (state.getMaterial().blocksMovement()) {
				if (Material.LEAVES.equals(state.getMaterial()) && --result != 0) { 
					continue;
				}
				return 0f;
			}
		}

		return result * 0.1f;
	}

	/**
	 * Ray traces through the world from sound to listener, and returns the
	 * amount of occlusion the sound should have. Note that it ignores the block
	 * which the sound plays from inside (so sounds playing from inside blocks
	 * don't sound occluded).
	 *
	 * @param world
	 *            The world
	 * @param sound
	 *            The position the sound plays from
	 * @param listener
	 *            The listener's position (the player)
	 * @return A double representing the amount of occlusion to apply to the
	 *         sound
	 */
	public static double getSoundOcclusion(World world, Vec3d sound, Vec3d listener) {
		double occludedPercent = 0.0D;

		// Fixes some funky things
		sound = sound.addVector(0.1, 0.1, 0.1);

		if (!Double.isNaN(sound.x) && !Double.isNaN(sound.y) && !Double.isNaN(sound.z)) {
			if (!Double.isNaN(listener.x) && !Double.isNaN(listener.y) && !Double.isNaN(listener.z)) {
				int listenerX = MathHelper.floor(listener.x);
				int listenerY = MathHelper.floor(listener.y);
				int listenerZ = MathHelper.floor(listener.z);
				int soundX = MathHelper.floor(sound.x);
				int soundY = MathHelper.floor(sound.y);
				int soundZ = MathHelper.floor(sound.z);
				int countDown = 200;

				while (countDown-- >= 0) {
					if (Double.isNaN(sound.x) || Double.isNaN(sound.y) || Double.isNaN(sound.z)) {
						return occludedPercent;
					}

					if (soundX == listenerX && soundY == listenerY && soundZ == listenerZ) {
						return occludedPercent;
					}

					boolean shouldChangeX = true;
					boolean shouldChangeY = true;
					boolean shouldChangeZ = true;
					double newX = 999.0D;
					double newY = 999.0D;
					double newZ = 999.0D;

					if (listenerX == soundX) {
						shouldChangeX = false;
					}

					if (shouldChangeX) {
						newX = (double) soundX + (listenerX > soundX ? 1.0D : 0.0D);
					}

					if (listenerY == soundY) {
						shouldChangeY = false;
					}

					if (shouldChangeY) {
						newY = (double) soundY + (listenerY > soundY ? 1.0D : 0.0D);
					}

					if (listenerZ == soundZ) {
						shouldChangeZ = false;
					}

					if (shouldChangeZ) {
						newZ = (double) soundZ + (listenerZ > soundZ ? 1.0D : 0.0D);
					}

					double xPercentChange = 999.0D;
					double yPercentChange = 999.0D;
					double zPercentChange = 999.0D;
					double xDifference = listener.x - sound.x;
					double yDifference = listener.y - sound.y;
					double zDifference = listener.z - sound.z;

					if (shouldChangeX) {
						xPercentChange = (newX - sound.x) / xDifference;
					}

					if (shouldChangeY) {
						yPercentChange = (newY - sound.y) / yDifference;
					}

					if (shouldChangeZ) {
						zPercentChange = (newZ - sound.z) / zDifference;
					}

					byte whichToChange;

					if (xPercentChange < yPercentChange && xPercentChange < zPercentChange) {
						if (listenerX > soundX) {
							whichToChange = 4;
						} else {
							whichToChange = 5;
						}

						sound = new Vec3d(newX, sound.y + yDifference * xPercentChange, sound.z + zDifference * xPercentChange);
					} else if (yPercentChange < zPercentChange) {
						if (listenerY > soundY) {
							whichToChange = 0;
						} else {
							whichToChange = 1;
						}

						sound = new Vec3d(sound.x + xDifference * yPercentChange, newY, sound.z + zDifference * yPercentChange);
					} else {
						if (listenerZ > soundZ) {
							whichToChange = 2;
						} else {
							whichToChange = 3;
						}

						sound = new Vec3d(sound.x + xDifference * zPercentChange, sound.y + yDifference * zPercentChange, newZ);
					}

					Vec3d vec32 = new Vec3d(MathHelper.floor(sound.x), MathHelper.floor(sound.y), MathHelper.floor(sound.z));
					soundX = (int) vec32.x;

					if (whichToChange == 5) {
						--soundX;
						vec32 = vec32.addVector(1.0, 0.0, 0.0);
					}

					soundY = (int) vec32.y;

					if (whichToChange == 1) {
						--soundY;
						vec32 = vec32.addVector(0.0, 1.0, 0.0);
					}

					soundZ = (int) vec32.z;

					if (whichToChange == 3) {
						--soundZ;
						vec32 = vec32.addVector(0.0, 0.0, 1.0);
					}

					BlockPos pos = new BlockPos(soundX, soundY, soundZ);
					IBlockState state = world.getBlockState(pos);
					Block block = world.getBlockState(pos).getBlock();
					int meta = block.getMetaFromState(state);
					Material material = state.getMaterial();

					if (block != null && block != Blocks.AIR && state.getBoundingBox(world, pos) != Block.NULL_AABB && block.canCollideCheck(state, false)) {
						RayTraceResult rayTrace = state.collisionRayTrace(world, pos, sound, listener);

						if (rayTrace != null) {
							// Check for custom occlusion blocks
							Double value = getCustomValue(block, meta, SoundFiltersConfig.CUSTOM_OCCLUSION);

							if (value != null) {
								occludedPercent += value.doubleValue() * 0.1D;
							} else if (occludedPercent < 0.7D) {
								occludedPercent += material.isOpaque() ? 0.1D : 0.05D;
							}

							if (occludedPercent > 0.98D) {
								return 0.98D;
							}
						}
					}
				}

				return occludedPercent;
			} else {
				return occludedPercent;
			}
		} else {
			return occludedPercent;
		}
	}

	private static Double getCustomValue(Block block, int meta, Map<BlockMeta,Double> customMap) {
		BlockMeta blockInfo = new BlockMeta(block, meta);
		Double value = customMap.get(blockInfo);

		if (value == null) {
			blockInfo = new BlockMeta(block, SoundFiltersConfig.ALL_METAS);
			value = customMap.get(blockInfo);
		}
		return value;
	}
}
