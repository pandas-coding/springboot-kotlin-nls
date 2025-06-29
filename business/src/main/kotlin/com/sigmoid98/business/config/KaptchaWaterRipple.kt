package com.sigmoid98.business.config

import com.google.code.kaptcha.GimpyEngine
import com.google.code.kaptcha.util.Configurable
import com.jhlabs.image.RippleFilter
import com.sigmoid98.business.extensions.use
import java.awt.image.BufferedImage
import kotlin.random.Random

class KaptchaWaterRipple : Configurable(), GimpyEngine {

    override fun getDistortedImage(baseImage: BufferedImage?): BufferedImage? {
        val noiseProducer = config.noiseImpl

        return BufferedImage(
            baseImage?.width ?: 0,
            baseImage?.height ?: 0,
            BufferedImage.TYPE_INT_ARGB,
        ).apply {
            createGraphics().use { graph ->
                val effectImage = RippleFilter().apply {
                    xAmplitude = 7.6F
                    yAmplitude = Random.nextFloat() + 1.0F
                    edgeAction = 1
                }.filter(baseImage, null)

                graph.drawImage(effectImage, 0, 0, null, null)
            }

            noiseProducer.makeNoise(this, 0.1f, 0.1f, 0.25f, 0.25f)
            noiseProducer.makeNoise(this, 0.1f, 0.25f, 0.5f, 0.9f)
        }
    }

}