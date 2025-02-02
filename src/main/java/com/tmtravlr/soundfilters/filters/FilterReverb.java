package com.tmtravlr.soundfilters.filters;

import org.lwjgl.openal.EFX10;

public class FilterReverb extends BaseFilter
{
    public float density = 1.0F;
    public float diffusion = 1.0F;
    public float gain = 0.32F;
    public float gainHF = 0.89F;
    public float decayTime = 1.49F;
    public float decayHFRatio = 0.83F;
    public float reflectionsGain = 0.05F;
    public float reflectionsDelay = 0.007F;
    public float lateReverbGain = 1.26F;
    public float lateReverbDelay = 0.011F;
    public float airAbsorptionGainHF = 0.994F;
    public float roomRolloffFactor = 0.0F;
    public int decayHFLimit = 1;

    @Override
	public void loadFilter()
    {
        if (!this.isLoaded)
        {
            this.isLoaded = true;
            this.id = EFX10.alGenEffects();
            this.slot = EFX10.alGenAuxiliaryEffectSlots();
        }
    }

    @Override
	public void checkParameters()
    {
        if (this.density < 0.0F)
        {
            this.density = 0.0F;
        }

        if (this.density > 1.0F)
        {
            this.density = 1.0F;
        }

        if (this.diffusion < 0.0F)
        {
            this.diffusion = 0.0F;
        }

        if (this.diffusion > 1.0F)
        {
            this.diffusion = 1.0F;
        }

        if (this.gain < 0.0F)
        {
            this.gain = 0.0F;
        }

        if (this.gain > 1.0F)
        {
            this.gain = 1.0F;
        }

        if (this.gainHF < 0.0F)
        {
            this.gainHF = 0.0F;
        }

        if (this.gainHF > 1.0F)
        {
            this.gainHF = 1.0F;
        }

        if (this.decayTime < 0.1F)
        {
            this.decayTime = 0.1F;
        }

        if (this.decayTime > 20.0F)
        {
            this.decayTime = 20.0F;
        }

        if (this.decayHFRatio < 0.1F)
        {
            this.decayHFRatio = 0.1F;
        }

        if (this.decayHFRatio > 2.0F)
        {
            this.decayHFRatio = 2.0F;
        }

        if (this.reflectionsGain < 0.0F)
        {
            this.reflectionsGain = 0.0F;
        }

        if (this.reflectionsGain > 3.16F)
        {
            this.reflectionsGain = 3.16F;
        }

        if (this.reflectionsDelay < 0.0F)
        {
            this.reflectionsDelay = 0.0F;
        }

        if (this.reflectionsDelay > 0.3F)
        {
            this.reflectionsDelay = 0.3F;
        }

        if (this.lateReverbGain < 0.0F)
        {
            this.lateReverbGain = 0.0F;
        }

        if (this.lateReverbGain > 10.0F)
        {
            this.lateReverbGain = 10.0F;
        }

        if (this.lateReverbDelay < 0.0F)
        {
            this.lateReverbDelay = 0.0F;
        }

        if (this.lateReverbDelay > 0.1F)
        {
            this.lateReverbDelay = 0.1F;
        }

        if (this.airAbsorptionGainHF < 0.892F)
        {
            this.airAbsorptionGainHF = 0.892F;
        }

        if (this.airAbsorptionGainHF > 1.0F)
        {
            this.airAbsorptionGainHF = 1.0F;
        }

        if (this.roomRolloffFactor < 0.0F)
        {
            this.roomRolloffFactor = 0.0F;
        }

        if (this.roomRolloffFactor > 10.0F)
        {
            this.roomRolloffFactor = 10.0F;
        }

        if (this.decayHFLimit < 0)
        {
            this.decayHFLimit = 0;
        }

        if (this.decayHFLimit > 1)
        {
            this.decayHFLimit = 1;
        }
    }

    @Override
	public void loadParameters()
    {
        this.checkParameters();

        if (!this.isLoaded)
        {
            this.loadFilter();
        }

        EFX10.alAuxiliaryEffectSlotf(this.slot, 2, 0.0F);
        EFX10.alEffecti(this.id, 32769, 1);
        EFX10.alEffectf(this.id, 1, this.density);
        EFX10.alEffectf(this.id, 2, this.diffusion);
        EFX10.alEffectf(this.id, 3, this.gain);
        EFX10.alEffectf(this.id, 4, this.gainHF);
        EFX10.alEffectf(this.id, 5, this.decayTime);
        EFX10.alEffectf(this.id, 6, this.decayHFRatio);
        EFX10.alEffectf(this.id, 7, this.reflectionsGain);
        EFX10.alEffectf(this.id, 8, this.reflectionsDelay);
        EFX10.alEffectf(this.id, 9, this.lateReverbGain);
        EFX10.alEffectf(this.id, 10, this.lateReverbDelay);
        EFX10.alEffectf(this.id, 11, this.airAbsorptionGainHF);
        EFX10.alEffectf(this.id, 12, this.roomRolloffFactor);
        EFX10.alEffecti(this.id, 13, this.decayHFLimit);
        EFX10.alAuxiliaryEffectSloti(this.slot, 1, this.id);
        EFX10.alAuxiliaryEffectSlotf(this.slot, 2, 1.0F);
    }
}
