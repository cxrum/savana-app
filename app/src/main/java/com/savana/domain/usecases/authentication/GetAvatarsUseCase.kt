package com.savana.domain.usecases.authentication

import com.savana.domain.models.AvatarData
import java.net.URL

class GetAvatarsUseCase {
    private val avatarPlaceholder= listOf(
        AvatarData(
            id = 1,
            url = URL("https://fakeimg.pl/600x600?text=image&font=bebas&font_size=16")
        ),
        AvatarData(
            id = 2,
            url = URL("https://fakeimg.pl/600x600?text=image&font=bebas&font_size=16")
        ),
        AvatarData(
            id = 3,
            url = URL("https://fakeimg.pl/600x600?text=image&font=bebas&font_size=16")
        ),
        AvatarData(
            id = 4,
            url = URL("https://fakeimg.pl/600x600?text=image&font=bebas&font_size=16")
        ),
        AvatarData(
            id = 5,
            url = URL("https://fakeimg.pl/600x600?text=image&font=bebas&font_size=16")
        ),
    )

    suspend operator fun invoke(): List<AvatarData>{
        return avatarPlaceholder
    }

}