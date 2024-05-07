package real_word_projects.json

import com.google.gson.Gson

data class Contacts(
    val email: String,
    val phone: String
)

/**
 * Data class representing a user.
 * Inner class Contacts is used to represent contact details of the user.
 */
data class User(
    val name: String,
    val age: Int,
    val contacts: Contacts,
    val attributes: Map<String, Any>?  // Optional dynamic attributes
) {
    /**
     * Data class for storing user contact details.
     */

}

/**
 * Main function to deserialize JSON into a User object and display the data.
 */
fun main() {
    val json = """
    {
      "name": "John Doe",
      "age": 30,
      "contacts": {
        "email": "john@example.com",
        "phone": "1234567890"
      },
      "attributes": {
        "height": 175,
        "weight": 70,
        "hobbies": ["reading", "cycling", "hiking"]
      }
    }
    """
    val gson = Gson()
    val user = gson.fromJson(json, User::class.java)  // Deserialize JSON directly into User object

    println("Name: ${user.name}")
    println("Age: ${user.age}")
    println("Email: ${user.contacts.email}")
    println("Phone: ${user.contacts.phone}")
    println("Attributes:")
    user.attributes?.forEach { (key, value) ->
        println("  $key: $value")
    }

    println(user.contacts.javaClass)

    println(user.attributes!!::class.qualifiedName)
}
