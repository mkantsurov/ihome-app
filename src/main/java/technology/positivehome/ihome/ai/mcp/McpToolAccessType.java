package technology.positivehome.ihome.ai.mcp;

/**
 * Categorises an MCP tool by whether its data is exposed via the unauthenticated
 * guest controller ({@link #PUBLIC_READ}) or not ({@link #RESTRICTED_READ}),
 * and whether it performs a write operation ({@link #WRITE}).
 *
 * <p>The defining characteristic of {@link #PUBLIC_READ} is that the tool's data
 * is <b>also served by the guest controller</b> (no authentication required on
 * that endpoint). Within the AI chat this means it is accessible to all
 * authenticated users (including {@code AUTHORIZED_GUEST}), but that is a
 * <b>consequence</b> of the guest-controller exposure, not the primary definition.
 *
 * <p>Three tiers govern AI chat access:
 * <ul>
 *   <li>{@link #PUBLIC_READ} — data <b>also</b> exposed via the guest controller.
 *       Within the AI chat: accessible to all authenticated users.</li>
 *   <li>{@link #RESTRICTED_READ} — data <b>not</b> on the guest controller.
 *       Within the AI chat: hidden from {@code AUTHORIZED_GUEST}, available to
 *       all other authenticated roles.</li>
 *   <li>{@link #WRITE} — write operations restricted to {@code ADMIN} and
 *       {@code SUPERVISOR}.</li>
 * </ul>
 *
 * <p>{@link #READ} is a legacy alias that maps to the {@code READ} security tier.
 */
public enum McpToolAccessType {

    /**
     * Read-only tool whose data is also served by the unauthenticated guest controller.
     * Within the AI chat this makes it accessible to all authenticated users
     * (including AUTHORIZED_GUEST), but the defining trait is the guest-controller exposure.
     */
    PUBLIC_READ,

    /**
     * Read-only tool whose data is <b>not</b> exposed via the guest controller.
     * Within the AI chat: hidden from AUTHORIZED_GUEST, available to all other
     * authenticated roles (UNDEFINED, ADMIN, SUPERVISOR, etc.).
     */
    RESTRICTED_READ,

    /**
     * Legacy read-only alias. Maps to the {@code READ} security tier.
     */
    READ,

    /**
     * Write tool restricted to ADMIN and SUPERVISOR.
     */
    WRITE
}
