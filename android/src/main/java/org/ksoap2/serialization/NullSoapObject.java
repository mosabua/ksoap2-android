package org.ksoap2.serialization;

/**
 * A class that implements only {@link NullSoapObject#toString()}.
 * This is useful in the case where you have a {@link SoapObject} representing an optional
 * property in your SOAP response.<br/><br/>
 *
 * Example:
 * <pre>
 * <code>
 * private String getAge(SoapObject person) {
 *   return person.getPropertySafely("age").toString();
 * }
 * </code>
 * </pre>
 * <ul>
 * <li> When the person object has an {@code age} property, the {@code age} will be returned. </li>
 * <li>
 *   When the person object does not have an {@code age} property,
 *   {@link SoapObject#getPropertySafely(String)}
 *   returns a NullSoapObject, which in turn returns {@code null} for {@link NullSoapObject#toString()}.
 * </li>
 * </ul>
 * Now it is safe to always try and get the {@code age} property (assuming your downstream
 * code can handle {@code age}).
 */

public class NullSoapObject {
  /**
   * Overridden specifically to always return null.
   * See the example in this class's description as to how this can be useful.
   *
   * @return {@code null}
   * @see SoapObject#getPropertySafely(String)
   */
  public String toString() {
    return null;
  }
}
