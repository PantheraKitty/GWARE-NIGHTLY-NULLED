package javassist.scopedpool;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class SoftValueHashMap<K, V> implements Map<K, V> {
   private Map<K, SoftValueHashMap.SoftValueRef<K, V>> hash;
   private ReferenceQueue<V> queue;

   public Set<Entry<K, V>> entrySet() {
      this.processQueue();
      Set<Entry<K, V>> ret = new HashSet();
      Iterator var2 = this.hash.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<K, SoftValueHashMap.SoftValueRef<K, V>> e = (Entry)var2.next();
         ret.add(new SimpleImmutableEntry(e.getKey(), ((SoftValueHashMap.SoftValueRef)e.getValue()).get()));
      }

      return ret;
   }

   private void processQueue() {
      Reference ref;
      if (!this.hash.isEmpty()) {
         while((ref = this.queue.poll()) != null) {
            if (ref instanceof SoftValueHashMap.SoftValueRef) {
               SoftValueHashMap.SoftValueRef que = (SoftValueHashMap.SoftValueRef)ref;
               if (ref == this.hash.get(que.key)) {
                  this.hash.remove(que.key);
               }
            }
         }
      }

   }

   public SoftValueHashMap(int initialCapacity, float loadFactor) {
      this.queue = new ReferenceQueue();
      this.hash = new ConcurrentHashMap(initialCapacity, loadFactor);
   }

   public SoftValueHashMap(int initialCapacity) {
      this.queue = new ReferenceQueue();
      this.hash = new ConcurrentHashMap(initialCapacity);
   }

   public SoftValueHashMap() {
      this.queue = new ReferenceQueue();
      this.hash = new ConcurrentHashMap();
   }

   public SoftValueHashMap(Map<K, V> t) {
      this(Math.max(2 * t.size(), 11), 0.75F);
      this.putAll(t);
   }

   public int size() {
      this.processQueue();
      return this.hash.size();
   }

   public boolean isEmpty() {
      this.processQueue();
      return this.hash.isEmpty();
   }

   public boolean containsKey(Object key) {
      this.processQueue();
      return this.hash.containsKey(key);
   }

   public V get(Object key) {
      this.processQueue();
      return this.valueOrNull((SoftValueHashMap.SoftValueRef)this.hash.get(key));
   }

   public V put(K key, V value) {
      this.processQueue();
      return this.valueOrNull((SoftValueHashMap.SoftValueRef)this.hash.put(key, SoftValueHashMap.SoftValueRef.create(key, value, this.queue)));
   }

   public V remove(Object key) {
      this.processQueue();
      return this.valueOrNull((SoftValueHashMap.SoftValueRef)this.hash.remove(key));
   }

   public void clear() {
      this.processQueue();
      this.hash.clear();
   }

   public boolean containsValue(Object arg0) {
      this.processQueue();
      if (null == arg0) {
         return false;
      } else {
         Iterator var2 = this.hash.values().iterator();

         SoftValueHashMap.SoftValueRef e;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            e = (SoftValueHashMap.SoftValueRef)var2.next();
         } while(null == e || !arg0.equals(e.get()));

         return true;
      }
   }

   public Set<K> keySet() {
      this.processQueue();
      return this.hash.keySet();
   }

   public void putAll(Map<? extends K, ? extends V> arg0) {
      this.processQueue();
      Iterator var2 = arg0.keySet().iterator();

      while(var2.hasNext()) {
         K key = var2.next();
         this.put(key, arg0.get(key));
      }

   }

   public Collection<V> values() {
      this.processQueue();
      List<V> ret = new ArrayList();
      Iterator var2 = this.hash.values().iterator();

      while(var2.hasNext()) {
         SoftValueHashMap.SoftValueRef<K, V> e = (SoftValueHashMap.SoftValueRef)var2.next();
         ret.add(e.get());
      }

      return ret;
   }

   private V valueOrNull(SoftValueHashMap.SoftValueRef<K, V> rtn) {
      return null == rtn ? null : rtn.get();
   }

   private static class SoftValueRef<K, V> extends SoftReference<V> {
      public K key;

      private SoftValueRef(K key, V val, ReferenceQueue<V> q) {
         super(val, q);
         this.key = key;
      }

      private static <K, V> SoftValueHashMap.SoftValueRef<K, V> create(K key, V val, ReferenceQueue<V> q) {
         return val == null ? null : new SoftValueHashMap.SoftValueRef(key, val, q);
      }
   }
}
