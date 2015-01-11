from google.appengine.ext import ndb

# import yawp from yawp
# yawp('/people').query().fetch()
# http://stackoverflow.com/questions/10709893/ndb-expando-model-with-dynamic-textproperty
# use expando

def yawp(path):
    def _get_kind(cls):
        return path

    Model = type(path, (ndb.Expando,), {})
    Model._get_kind = classmethod(_get_kind)
    return Model
