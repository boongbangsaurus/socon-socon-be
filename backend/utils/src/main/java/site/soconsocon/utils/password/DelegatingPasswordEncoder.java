package site.soconsocon.utils.password;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DelegatingPasswordEncoder implements PasswordEncoder {
    private static final String DEFAULT_ID_PREFIX = "{";
    private static final String DEFAULT_ID_SUFFIX = "}";
    private final String idPrefix;
    private final String idSuffix;
    private final String idForEncode;
    private final PasswordEncoder passwordEncoderForEncode;
    private final Map<String, PasswordEncoder> idToPasswordEncoder;
    private PasswordEncoder defaultPasswordEncoderForMatches;

    public DelegatingPasswordEncoder(String idForEncode, Map<String, PasswordEncoder> idToPasswordEncoder) {
        this(idForEncode, idToPasswordEncoder, "{", "}");
    }

    public DelegatingPasswordEncoder(String idForEncode, Map<String, PasswordEncoder> idToPasswordEncoder, String idPrefix, String idSuffix) {
        this.defaultPasswordEncoderForMatches = new UnmappedIdPasswordEncoder();
        if (idForEncode == null) {
            throw new IllegalArgumentException("idForEncode cannot be null");
        } else if (idPrefix == null) {
            throw new IllegalArgumentException("prefix cannot be null");
        } else if (idSuffix != null && !idSuffix.isEmpty()) {
            if (idPrefix.contains(idSuffix)) {
                throw new IllegalArgumentException("idPrefix " + idPrefix + " cannot contain idSuffix " + idSuffix);
            } else if (!idToPasswordEncoder.containsKey(idForEncode)) {
                throw new IllegalArgumentException("idForEncode " + idForEncode + "is not found in idToPasswordEncoder " + idToPasswordEncoder);
            } else {
                Iterator var5 = idToPasswordEncoder.keySet().iterator();

                while(var5.hasNext()) {
                    String id = (String)var5.next();
                    if (id != null) {
                        if (!idPrefix.isEmpty() && id.contains(idPrefix)) {
                            throw new IllegalArgumentException("id " + id + " cannot contain " + idPrefix);
                        }

                        if (id.contains(idSuffix)) {
                            throw new IllegalArgumentException("id " + id + " cannot contain " + idSuffix);
                        }
                    }
                }

                this.idForEncode = idForEncode;
                this.passwordEncoderForEncode = (PasswordEncoder)idToPasswordEncoder.get(idForEncode);
                this.idToPasswordEncoder = new HashMap(idToPasswordEncoder);
                this.idPrefix = idPrefix;
                this.idSuffix = idSuffix;
            }
        } else {
            throw new IllegalArgumentException("suffix cannot be empty");
        }
    }

    public void setDefaultPasswordEncoderForMatches(PasswordEncoder defaultPasswordEncoderForMatches) {
        if (defaultPasswordEncoderForMatches == null) {
            throw new IllegalArgumentException("defaultPasswordEncoderForMatches cannot be null");
        } else {
            this.defaultPasswordEncoderForMatches = defaultPasswordEncoderForMatches;
        }
    }

    public String encode(CharSequence rawPassword) {
        String var10000 = this.idPrefix;
        return var10000 + this.idForEncode + this.idSuffix + this.passwordEncoderForEncode.encode(rawPassword);
    }

    public boolean matches(CharSequence rawPassword, String prefixEncodedPassword) {
        if (rawPassword == null && prefixEncodedPassword == null) {
            return true;
        } else {
            String id = this.extractId(prefixEncodedPassword);
            PasswordEncoder delegate = (PasswordEncoder)this.idToPasswordEncoder.get(id);
            if (delegate == null) {
                return this.defaultPasswordEncoderForMatches.matches(rawPassword, prefixEncodedPassword);
            } else {
                String encodedPassword = this.extractEncodedPassword(prefixEncodedPassword);
                return delegate.matches(rawPassword, encodedPassword);
            }
        }
    }

    private String extractId(String prefixEncodedPassword) {
        if (prefixEncodedPassword == null) {
            return null;
        } else {
            int start = prefixEncodedPassword.indexOf(this.idPrefix);
            if (start != 0) {
                return null;
            } else {
                int end = prefixEncodedPassword.indexOf(this.idSuffix, start);
                return end < 0 ? null : prefixEncodedPassword.substring(start + this.idPrefix.length(), end);
            }
        }
    }

    public boolean upgradeEncoding(String prefixEncodedPassword) {
        String id = this.extractId(prefixEncodedPassword);
        if (!this.idForEncode.equalsIgnoreCase(id)) {
            return true;
        } else {
            String encodedPassword = this.extractEncodedPassword(prefixEncodedPassword);
            return ((PasswordEncoder)this.idToPasswordEncoder.get(id)).upgradeEncoding(encodedPassword);
        }
    }

    private String extractEncodedPassword(String prefixEncodedPassword) {
        int start = prefixEncodedPassword.indexOf(this.idSuffix);
        return prefixEncodedPassword.substring(start + this.idSuffix.length());
    }

    private class UnmappedIdPasswordEncoder implements PasswordEncoder {
        private UnmappedIdPasswordEncoder() {
        }

        public String encode(CharSequence rawPassword) {
            throw new UnsupportedOperationException("encode is not supported");
        }

        public boolean matches(CharSequence rawPassword, String prefixEncodedPassword) {
            String id = DelegatingPasswordEncoder.this.extractId(prefixEncodedPassword);
            throw new IllegalArgumentException("There is no PasswordEncoder mapped for the id \"" + id + "\"");
        }
    }
}

