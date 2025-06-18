package core.ecommerce.entity;

public enum Role {
    ADMIN,
    CLIENT;

    public static Role fromString(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
