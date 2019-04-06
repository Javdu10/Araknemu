package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemSet;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemSetRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * SQL implementation for {@link ItemSet} repository
 */
final class SqlItemSetRepository implements ItemSetRepository {
    private class Loader implements RepositoryUtils.Loader<ItemSet> {
        @Override
        public ItemSet create(ResultSet rs) throws SQLException {
            return new ItemSet(
                rs.getInt("ITEM_SET_ID"),
                rs.getString("ITEM_SET_NAME"),
                bonusTransformer.unserialize(rs.getString("ITEM_SET_BONUS"))
            );
        }

        @Override
        public ItemSet fillKeys(ItemSet entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<ItemSet> utils;

    final private Transformer<List<List<ItemTemplateEffectEntry>>> bonusTransformer;

    public SqlItemSetRepository(ConnectionPool pool, Transformer<List<List<ItemTemplateEffectEntry>>> bonusTransformer) {
        this.bonusTransformer = bonusTransformer;
        this.pool = new ConnectionPoolUtils(pool);
        utils = new RepositoryUtils<>(this.pool, new SqlItemSetRepository.Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE `ITEM_SET` (" +
                    "`ITEM_SET_ID` INTEGER PRIMARY KEY," +
                    "`ITEM_SET_NAME` VARCHAR(50)," +
                    "`ITEM_SET_BONUS` TEXT" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE ITEM_SET");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public ItemSet get(ItemSet entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public ItemSet get(int id) {
        return utils.findOne(
            "SELECT * FROM ITEM_SET WHERE ITEM_SET_ID = ?",
            stmt -> stmt.setInt(1, id)
        );
    }

    @Override
    public boolean has(ItemSet entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM ITEM_SET WHERE ITEM_SET_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Collection<ItemSet> load() {
        return utils.findAll("SELECT * FROM ITEM_SET");
    }
}