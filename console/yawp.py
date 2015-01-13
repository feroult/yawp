from google.appengine.ext import ndb
from google.appengine.api import datastore
from google.appengine.api import datastore_types
from google.appengine.api import datastore_errors
from functools import partial

# import yawp from yawp
# yawp('/people').query().fetch()
# http://stackoverflow.com/questions/10709893/ndb-expando-model-with-dynamic-textproperty
# http://stackoverflow.com/questions/19842671/migrating-data-when-changing-an-ndb-fields-property-type/19848970#19848970

def change_property_to_text(key, entity):
    entity[key] = datastore_types.Text(entity[key])

def yawp(path):
    # public
    def migrate(self, page_size, fn_str, *args, **keywords):
        fn = get_fn(fn_str, *args, **keywords)

        q = self.query()
        cursor = None
        while True:
            keys, cursor, more = q.fetch_page(page_size, keys_only=True, start_cursor=cursor)
            entities = get_entities([key.to_old_key() for key in keys])
            for entity in entities:
                fn(entity)
            put_entities(entities)
            if not more:
                break

    def put():
        print 'disabled'

    # private
    def get_fn(fn_str, *args, **keywords):
        possibles = globals().copy()
        possibles.update(locals())
        fn = possibles.get(fn_str)
        return partial(fn, *args, **keywords)

    def get_entities(keys):
        rpc = datastore.GetRpcFromKwargs({})
        keys, multiple = datastore.NormalizeAndTypeCheckKeys(keys)
        entities = None
        try:
            entities = datastore.Get(keys, rpc=rpc)
        except datastore_errors.EntityNotFoundError:
            assert not multiple

        return entities

    def put_entities(entities):
        rpc = datastore.GetRpcFromKwargs({})
        keys = datastore.Put(entities, rpc=rpc)
        return keys

    # ndb model mapping
    def _get_kind(cls):
        return path

    Model = type(path, (ndb.Model,), {'put':put})
    Model._get_kind = classmethod(_get_kind)
    Model.migrate = classmethod(migrate)
    return Model
