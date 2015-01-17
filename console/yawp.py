from google.appengine.ext import ndb
from google.appengine.api import datastore
from google.appengine.api import datastore_types
from google.appengine.api import datastore_errors
from functools import partial

# import yawp from yawp
# yawp('/people').query().fetch()
# http://stackoverflow.com/questions/10709893/ndb-expando-model-with-dynamic-textproperty
# http://stackoverflow.com/questions/19842671/migrating-data-when-changing-an-ndb-fields-property-type/19848970#19848970

def get_key(yawpId):
    split = yawpId.split('/')
    kind = '/' + split[1]
    id = long(split[2])
    return ndb.Key(kind, id)

def get_id(ndbKey):
    return ndbKey.kind() + '/' + str(ndbKey.id())


def change_property_to_text(key, entity):
    print('.')
    entity[key] = datastore_types.Text(entity[key])

def get_entities(new_keys):
    keys = [key.to_old_key() for key in new_keys]
    rpc = datastore.GetRpcFromKwargs({})
    keys, multiple = datastore.NormalizeAndTypeCheckKeys(keys)
    entities = None
    try:
        entities = datastore.Get(keys, rpc=rpc)
    except datastore_errors.EntityNotFoundError:
        assert not multiple

    return entities

def get_entity(key):
    entities = get_entities([key])
    if len(entities) == 0:
        return None
    return entities[0]

def put_entities(entities):
    rpc = datastore.GetRpcFromKwargs({})
    keys = datastore.Put(entities, rpc=rpc)
    return keys

def put_entity(entity):
    return put_entities([entity])

def yawp(path):
    # public
    def delete_all(self, page_size=100):
        q = self.query()
        cursor = None
        while True:
            keys, cursor, more = q.fetch_page(page_size, keys_only=True, start_cursor=cursor)
            ndb.delete_multi(keys)
            if not more:
                break

    def migrate(self, page_size, fn):
        #fn = get_fn(fn_str, *args, **keywords)
        q = self.query()
        cursor = None
        while True:
            keys, cursor, more = q.fetch_page(page_size, keys_only=True, start_cursor=cursor)
            entities = get_entities(keys)
            for entity in entities:
                fn(entity)
            put_entities(entities)
            if not more:
                break
    # private
    def get_fn(fn_str, *args, **keywords):
        possibles = globals().copy()
        possibles.update(locals())
        fn = possibles.get(fn_str)
        print('fn', fn)
        return partial(fn, *args, **keywords)


    # ndb model mapping
    def _get_kind(cls):
        return path

    Model = type(path, (ndb.Expando,), {})
    Model._get_kind = classmethod(_get_kind)
    Model.delete_all = classmethod(delete_all)
    Model.migrate = classmethod(migrate)
    return Model
