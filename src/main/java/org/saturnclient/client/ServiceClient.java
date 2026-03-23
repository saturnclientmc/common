package org.saturnclient.client;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.saturnclient.client.player.SaturnPlayer;
import org.saturnclient.common.provider.Providers;
import org.saturnclient.common.ref.game.MinecraftClientRef;
import org.saturnclient.cosmetics.Cloaks;
import org.saturnclient.cosmetics.Hats;

import dev.selimaj.session.Session;

public class ServiceClient {
    private static Session session;
    public static UUID uuid;

    public static boolean connectTimeout() {
        try {
            session = Session.connect("wss://saturn-server.selimaj.dev", 10, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean authenticate() {
        MinecraftClientRef client = Providers.saturn.getClient();

        client.onClientStopping(() -> {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception e) {
                    Providers.saturn.logError("Unable to close Saturn session", e);
                }
            }
        });

        try {
            String accessToken = client.getAccessToken();
            uuid = client.getUuid();
            String username = client.getUsername();

            if (accessToken == null || uuid == null || username == null) {
                Providers.saturn.logError("No active Minecraft session found");
                return false;
            }

            Providers.saturn.logInfo("Authenticating with UUID: " + uuid);

            if (!connectTimeout()) {
                Providers.saturn.logError("Unable to authenticate: Session Server Timeout");
                return false;
            }

            ServiceMethods.Types.Player response = session.request(ServiceMethods.Authenticate, accessToken)
                    .get();

            for (String availableCloak : response.cloaks()) {
                Cloaks.availableCloaks.add(availableCloak);
            }

            for (String availableHat : response.hats()) {
                Hats.availableHats.add(availableHat);
            }

            eventHandlers();

            SaturnPlayer.set(new SaturnPlayer(uuid, username, response.cloak(), response.hat()));

            return true;
        } catch (Exception e) {
            Providers.saturn.logError("Authentication failed", e);
            return false;
        }
    }

    public static void setCloak(String itemId) {
        try {
            session.request(ServiceMethods.SetCloak, itemId).whenComplete((msg, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    try {
                        session.request(ServiceMethods.SendPlayer,
                                new ServiceMethods.Types.SendPlayerRequest(SaturnPlayer.getExternalUUIDAsString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Providers.saturn.logError("Failed to set cloak (service): ", e);
        }
    }

    public static void setHat(String itemId) {
        try {
            session.request(ServiceMethods.SetHat, itemId).whenComplete((msg, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    try {
                        session.request(ServiceMethods.SendPlayer,
                                new ServiceMethods.Types.SendPlayerRequest(SaturnPlayer.getExternalUUIDAsString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Providers.saturn.logError("Failed to set hat (service): ", e);
        }
    }

    public static void buyCloak(String itemId) {
        try {
            session.request(ServiceMethods.BuyCloak, itemId).whenComplete((msg, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    Cloaks.availableCloaks.add(itemId);
                }
            });
        } catch (Exception e) {
            Providers.saturn.logError("Failed to buy cloak (service): ", e);
        }
    }

    public static void buyHat(String itemId) {
        try {
            session.request(ServiceMethods.BuyHat, itemId).whenComplete((msg, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    Hats.availableHats.add(itemId);
                }
            });
        } catch (Exception e) {
            Providers.saturn.logError("Failed to buy hat (service): ", e);
        }
    }

    public static void emote(String emote) {
        try {
            session.request(ServiceMethods.Emote,
                    new ServiceMethods.Types.EmoteRequest(emote, SaturnPlayer.getExternalUUIDAsString()))
                    .whenComplete((msg, throwable) -> {
                        if (throwable != null) {
                            throwable.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            Providers.saturn.logError("Failed to emote (service): ", e);
        }
    }

    public static void eventHandlers() {
        session.onNotification(ServiceMethods.EmoteEvent, (data) -> {
            if (data == null) {
                return;
            }
            UUID from;
            try {
                from = UUID.fromString(data.from());
            } catch (Exception e) {
                return;
            }

            if (data.emote() != null && !data.emote().isEmpty()) {
                Providers.saturn.playEmote(from, data.emote());
            } else {
                Providers.saturn.playEmote(from, null);
            }
        });

        session.onNotification(ServiceMethods.Player, (player) -> {
            System.out.println(player);
            SaturnPlayer.set(player.toSaturnPlayer());
        });
    }

    public static SaturnPlayer getPlayer(UUID uuid, String name) {
        try {
            ServiceMethods.Types.Player player = session.request(ServiceMethods.GetPlayer, uuid.toString()).join();

            if (player == null)
                return null;

            return player.toSaturnPlayer(uuid, name);
        } catch (Exception e) {
            Providers.saturn.logError("Failed to get player", e);
        }

        return null;
    }
}
