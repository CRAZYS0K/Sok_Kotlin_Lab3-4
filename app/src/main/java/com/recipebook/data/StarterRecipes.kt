package com.recipebook.data

import androidx.sqlite.SQLiteConnection

object StarterRecipes {
    private class Item(
        val name: String,
        val category: Category,
        val minutes: Int,
        val servings: Int,
        val photo: String?,
        val ingredients: List<Triple<String, Double, String>>,
        val steps: List<String>,
    )

    private fun photo(name: String) = PhotoStorage.ASSET_PREFIX + "starter/$name.jpg"

    private val items = listOf(
        Item(
            "Сырники", Category.BREAKFAST, 25, 3, photo("syrniki"),
            listOf(
                Triple("Творог 5%", 500.0, "г"), Triple("Яйцо", 1.0, "шт."),
                Triple("Мука пшеничная", 60.0, "г"), Triple("Сахар", 40.0, "г"),
                Triple("Соль", 1.0, "щеп."), Triple("Масло для жарки", 30.0, "мл"),
            ),
            listOf(
                "Размять творог вилкой, добавить яйцо, сахар и соль.",
                "Всыпать муку и замесить мягкое тесто.",
                "Сформировать небольшие лепёшки и обвалять их в муке.",
                "Обжарить на среднем огне по 3–4 минуты с каждой стороны до золотистого цвета.",
            ),
        ),
        Item(
            "Борщ", Category.SOUP, 90, 6, photo("borsch"),
            listOf(
                Triple("Говядина на кости", 500.0, "г"), Triple("Свёкла", 2.0, "шт."),
                Triple("Капуста белокочанная", 300.0, "г"), Triple("Картофель", 3.0, "шт."),
                Triple("Морковь", 1.0, "шт."), Triple("Лук репчатый", 1.0, "шт."),
                Triple("Томатная паста", 2.0, "ст. л."), Triple("Вода", 2.5, "л"),
            ),
            listOf(
                "Сварить бульон из говядины, около часа.",
                "Нарезать свёклу соломкой и потушить с томатной пастой.",
                "Лук и морковь мелко нарезать и обжарить.",
                "Положить в бульон картофель кубиками, через 10 минут — капусту.",
                "Добавить свёклу и обжаренные овощи, варить ещё 10 минут.",
                "Дать борщу настояться 15 минут и подавать со сметаной.",
            ),
        ),
        Item(
            "Паста карбонара", Category.MAIN, 20, 2, photo("carbonara"),
            listOf(
                Triple("Спагетти", 200.0, "г"), Triple("Бекон", 100.0, "г"),
                Triple("Яичный желток", 3.0, "шт."), Triple("Пармезан", 50.0, "г"),
                Triple("Чёрный перец", 0.5, "ч. л."),
            ),
            listOf(
                "Отварить спагетти в подсоленной воде до состояния аль денте.",
                "Обжарить бекон до хрустящей корочки.",
                "Смешать желтки с тёртым пармезаном и перцем.",
                "Соединить горячую пасту с беконом, снять с огня и быстро вмешать соус.",
            ),
        ),
        Item(
            "Шарлотка", Category.BAKING, 50, 8, photo("charlotte"),
            listOf(
                Triple("Яблоки", 4.0, "шт."), Triple("Яйцо", 4.0, "шт."),
                Triple("Сахар", 200.0, "г"), Triple("Мука пшеничная", 200.0, "г"),
                Triple("Корица", 1.0, "ч. л."),
            ),
            listOf(
                "Очистить яблоки от сердцевины и нарезать дольками.",
                "Взбить яйца с сахаром до пышной светлой массы.",
                "Аккуратно вмешать муку лопаткой.",
                "Выложить яблоки в форму, посыпать корицей и залить тестом.",
                "Выпекать 40 минут при 180 °C.",
            ),
        ),
        Item(
            "Салат с киноа", Category.SALAD, 15, 2, null,
            listOf(
                Triple("Киноа", 100.0, "г"), Triple("Огурец", 1.0, "шт."),
                Triple("Помидоры черри", 150.0, "г"), Triple("Авокадо", 1.0, "шт."),
                Triple("Оливковое масло", 2.0, "ст. л."), Triple("Лимонный сок", 1.0, "ст. л."),
            ),
            listOf(
                "Промыть киноа и отварить 12 минут, затем остудить.",
                "Нарезать огурец, помидоры и авокадо.",
                "Смешать овощи с киноа, заправить маслом и лимонным соком.",
            ),
        ),
        Item(
            "Блины", Category.BREAKFAST, 40, 4, photo("pancakes"),
            listOf(
                Triple("Молоко", 500.0, "мл"), Triple("Яйцо", 2.0, "шт."),
                Triple("Мука пшеничная", 200.0, "г"), Triple("Сахар", 1.0, "ст. л."),
                Triple("Соль", 0.5, "ч. л."), Triple("Растительное масло", 2.0, "ст. л."),
            ),
            listOf(
                "Взбить яйца с сахаром и солью.",
                "Влить половину молока, всыпать муку и размешать без комков.",
                "Добавить оставшееся молоко и масло, дать тесту постоять 15 минут.",
                "Жарить блины на разогретой сковороде с двух сторон.",
            ),
        ),
    )

    fun insertInto(connection: SQLiteConnection) {
        for (item in items.asReversed()) {
            connection.prepare(
                "INSERT INTO recipes (name, category, cookingMinutes, servings, isFavorite, photoFileName) " +
                    "VALUES (?, ?, ?, ?, 0, ?)"
            ).use { statement ->
                statement.bindText(1, item.name)
                statement.bindText(2, item.category.name)
                statement.bindLong(3, item.minutes.toLong())
                statement.bindLong(4, item.servings.toLong())
                if (item.photo != null) statement.bindText(5, item.photo) else statement.bindNull(5)
                statement.step()
            }
            val recipeId = connection.prepare("SELECT last_insert_rowid()").use { statement ->
                statement.step()
                statement.getLong(0)
            }
            item.ingredients.forEachIndexed { position, (name, amount, unit) ->
                connection.prepare(
                    "INSERT INTO ingredients (recipeId, position, name, amount, unit) VALUES (?, ?, ?, ?, ?)"
                ).use { statement ->
                    statement.bindLong(1, recipeId)
                    statement.bindLong(2, position.toLong())
                    statement.bindText(3, name)
                    statement.bindDouble(4, amount)
                    statement.bindText(5, unit)
                    statement.step()
                }
            }
            item.steps.forEachIndexed { position, text ->
                connection.prepare("INSERT INTO steps (recipeId, position, text) VALUES (?, ?, ?)").use { statement ->
                    statement.bindLong(1, recipeId)
                    statement.bindLong(2, position.toLong())
                    statement.bindText(3, text)
                    statement.step()
                }
            }
        }
    }
}
