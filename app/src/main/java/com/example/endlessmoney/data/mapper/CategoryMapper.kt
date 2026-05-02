package com.example.endlessmoney.data.mapper

import com.example.endlessmoney.data.local.entity.CategoryEntity
import com.example.endlessmoney.domain.model.Category

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        icon = icon,
        color = color
    )
}