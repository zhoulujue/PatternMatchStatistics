object TestCase {

    public const val TEST_CONFIG = "{\"enabled_trackers\":[\"common_tracker\"],\"common_tracker\":{\"pattern\":[{\"type\":\"pageA\",\"actions\":[\"disappear\"],\"ids\":[\"1234567890xxx\"]},{\"type\":\"pageB\",\"actions\":[\"appear\"],\"ids\":[\"1234567890xxx\"]}],\"match_receivers\":[{\"id\":\"example\"}]}}"
    public const val TEST_CONFIG_FOR_ANY = "{\"enabled_trackers\":[\"common_tracker\"],\"common_tracker\":{\"pattern\":[{\"type\":\"pageA\",\"actions\":[\"disappear\"],\"ids\":[\"1234567890xxx\"],\"custome_info\":{\"key1\":\"value1\",\"key2\":\"value2\"}},{\"type\":\"any\",\"match_quantifier\":\"{1}\"},{\"type\":\"pageB\",\"actions\":[\"appear\"],\"ids\":[\"1234567890xxx\"]}],\"match_receivers\":[{\"id\":\"example\"}]}}"
}