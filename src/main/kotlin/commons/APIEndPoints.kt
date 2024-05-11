package commons

const val ALPACA_MARKET_WEBSOCKET_STREAM_URL = "wss://paper-api.alpaca.markets/stream"
enum class AlpacaAuthFields(fieldIdentifier: String) {
    API_KEY_ID("APCA-API-KEY-ID"),
    API_SECRET("APCA-API-SECRET-KEY")
}