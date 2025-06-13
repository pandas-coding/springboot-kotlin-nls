package com.sigmoid98.business.config

import com.google.code.kaptcha.impl.DefaultKaptcha
import com.google.code.kaptcha.util.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class KaptchaConfig {

    @Bean("defaultKaptcha")
    fun getDefaultKaptcha(): DefaultKaptcha {
        return DefaultKaptcha().apply {
            val properties = Properties().apply {
                setProperty("kaptcha.border", "no")
                setProperty("kaptcha.textproducer.font.color", "blue")
                setProperty("kaptcha.image.width", "90")
                setProperty("kaptcha.image.height", "28")
                setProperty("kaptcha.textproducer.font.size", "20")
                setProperty("kaptcha.session.key", "code")
                setProperty("kaptcha.textproducer.char.length", "4")
                setProperty("kaptcha.textproducer.font.names", "Arial")
                setProperty("kaptcha.noise.color", "255,96,0")
                setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise")
                setProperty("kaptcha.obscurificator.impl", KaptchaWaterRipple::class.qualifiedName)
                setProperty("kaptcha.background.impl", KaptchaNoBackground::class.qualifiedName)
            }
            this.config = Config(properties)
        }
    }

    @Bean("webKaptcha")
    fun getWebKaptcha(): DefaultKaptcha = DefaultKaptcha().apply {
        val properties = Properties().apply {
            setProperty("kaptcha.border", "no")
            setProperty("kaptcha.textproducer.font.color", "blue")
            setProperty("kaptcha.image.width", "90")
            setProperty("kaptcha.image.height", "45")
            setProperty("kaptcha.textproducer.font.size", "30")
            setProperty("kaptcha.session.key", "code")
            setProperty("kaptcha.textproducer.char.length", "4")
            setProperty("kaptcha.textproducer.font.names", "Arial")
            setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise")
            setProperty("kaptcha.obscurificator.impl", KaptchaWaterRipple::class.qualifiedName)
        }
        this.config = Config(properties)
    }
}