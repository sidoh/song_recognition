package org.sidoh.song_recognition.signature;

import java.util.Iterator;

import org.sidoh.song_recognition.signature.ConstellationMap.Star;
import org.sidoh.song_recognition.spectrogram.Spectrogram;

/**
 * This is used in conjunction with {@link ConstellationMapExtractor} to extract
 * {@link Star}s (Spectrogram peaks). It is fed potential peaks and the coordinates
 * that they reside at, and it keeps track of which ones it likes to keep (in
 * memory). 
 * 
 * It should support an operation called flush(), which should return an 
 * {@link Iterator} of {@link Star}s that it'd like to keep.
 * 
 * {@link StarBuffer}s are required to be thread safe.
 * 
 * @author chris
 */
public abstract class StarBuffer {
	
	public static abstract class Builder {
		protected static final double[] fairSizes   = {0.05d, 0.15, 0.40d};
		protected static final double[] fairWeights = {0.1d, 0.7d, 0.2d};
		
		protected double starDensityFactor;
		
		public Builder(double starDensityFactor) {
			this.starDensityFactor = starDensityFactor;
		}
		
		public Builder starDensityFactor(double starDensityFactor) {
			this.starDensityFactor = starDensityFactor;
			return this;
		}
		
		public EvenFrequencyBandsStarBuffer.Builder evenlyBanded(int numBands) {
			return new EvenFrequencyBandsStarBuffer.Builder(numBands, this);
		}
		
		public ManuallyBucketizedFrequencyBandsStarBuffer.Builder manuallyBanded(double[] bandSizes, double[] bandWeights) {
			return new ManuallyBucketizedFrequencyBandsStarBuffer.Builder(starDensityFactor, bandSizes, bandWeights, this);
		}
		
		public ManuallyBucketizedFrequencyBandsStarBuffer.Builder fairlyBanded() {
			return manuallyBanded(fairSizes, fairWeights);
		}
		
		public abstract StarBuffer create(Spectrogram spec);
	}
	
	protected final Spectrogram spec;

	public StarBuffer(Spectrogram spec) {
		this.spec = spec;
	}

	/**
	 * Offer a {@link Star} to this buffer. It may or may not accept it, and this 
	 * {@link Star} may or may not be returned by {@link #flush()} depending on
	 * how the internal logic works.
	 * 
	 * @param s
	 */
	public abstract void offerStar(Star s);
	
	/**
	 * Returns a list of stars it'd like to keep.
	 * 
	 * @return
	 */
	public abstract Iterable<Star> flush();
	
	public static StarBuffer.Builder coordinateAgnostic(double starDensityFactor) {
		return new CoordinateAgnosticStarBuffer.Builder(starDensityFactor);
	}
	
	public static StarBuffer.Builder evenlySpreadInTime(double starDensityFactor) {
		return new EvenlySpreadInTimeStarBuffer.Builder(starDensityFactor);
	}
	
	public static StarBuffer.Builder evenlySpreadInFrequency(double starDensityFactor) {
		return new EvenlySpreadInFrequencyStarBuffer.Builder(starDensityFactor);
	}
}
