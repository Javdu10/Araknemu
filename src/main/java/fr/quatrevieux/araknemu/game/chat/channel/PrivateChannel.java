package fr.quatrevieux.araknemu.game.chat.channel;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatException;
import fr.quatrevieux.araknemu.game.event.common.ConcealedMessage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;

/**
 * Channel for private messages
 */
final public class PrivateChannel implements Channel {
    final private PlayerService service;

    public PrivateChannel(PlayerService service) {
        this.service = service;
    }

    @Override
    public ChannelType type() {
        return ChannelType.PRIVATE;
    }

    @Override
    public void send(GamePlayer from, Message message) throws ChatException {
        if (!service.isOnline(message.target())) {
            throw new ChatException(ChatException.Error.USER_NOT_CONNECTED);
        }

        GamePlayer to = service.get(message.target());
        ConcealedMessage event = new ConcealedMessage(
            from,
            to,
            message.message(),
            message.items()
        );

        from.dispatch(event);
        to.dispatch(event);
    }
}