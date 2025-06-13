package com.sigmoid98.business.config

import com.google.code.kaptcha.BackgroundProducer
import com.google.code.kaptcha.util.Configurable
import com.sigmoid98.business.config.graphics.use
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage


class KaptchaNoBackground : Configurable(), BackgroundProducer {

    override fun addBackground(baseImage: BufferedImage): BufferedImage {
        val width = baseImage.width
        val height = baseImage.height

        return BufferedImage(width, height, BufferedImage.TYPE_INT_RGB).also { it ->
            it.createGraphics().use { graph ->
                // graph 的默认颜色通常是黑色。如果需要特定颜色，例如白色：
                // graph.color = Color.WHITE
                // 填充整个图像背景。
                graph.fill(Rectangle2D.Double(0.0, 0.0, width.toDouble(), height.toDouble()))

                // 将原始图像（包含验证码文本）绘制到新创建的带背景的图像上。
                graph.drawImage(baseImage, 0, 0, null)
            } // graph.dispose() 会在此处自动调用
        }
    }

}